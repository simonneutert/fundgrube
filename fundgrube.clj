(require '[cheshire.core :as json])
(require '[babashka.curl :as curl])
(require '[clojure.set :as cset])
(require '[clojure.edn :as edn])
(require '[babashka.fs :as fs])

(def fundgrube-tgram-api-key (System/getenv "FUNDGRUBE_TGRAM_API_KEY"))
(def fundgrube-tgram-channel (System/getenv "FUNDGRUBE_TGRAM_CHANNEL"))
(def fundgrube-outlet-ids (or (System/getenv "FUNDGRUBE_OUTLET_IDS") "418,576,798"))

(def url "https://www.mediamarkt.de/de/data/fundgrube/api/postings")
(def filename-current-results "data_fundgrube.edn")
(def filename-past-results "data_fundgrube_old.edn")

(defn pretty-spit [filename content]
  (->> content
       clojure.pprint/pprint
       with-out-str
       (spit filename)))

(defn slurp-read-edn
  [filename]
  (edn/read-string (slurp filename)))

(defn past-fundgrube-results
  []
  (slurp-read-edn filename-past-results))

(defn current-fundgrube-result
  []
  (slurp-read-edn filename-current-results))

(defn get-postings
  [url limit offset]
  (curl/get url {:headers {"Accept" "application/json"
                           "User-Agent" "Bacon/1.0"}
                 :query-params {"outletIds" fundgrube-outlet-ids
                                "recentFilter" "outlets"
                                "offset" offset
                                "limit" limit}}))

(defn extract-json-body
  [resp]
  (-> resp
      (get :body)
      (json/parse-string true)))

(defn get-json-data
  [url limit offset]
  (loop [collection []
         offset offset]
    (let [api-data (extract-json-body (get-postings url limit offset))]
      (cond
        (empty? (:postings api-data)) collection
        :else (recur (apply conj collection (:postings api-data)) (+ 50 offset))))))

(defn diff-fundgrube-results
  [current-fundgrube-result]
  (cset/difference (set (keys current-fundgrube-result))
                   (set (keys (past-fundgrube-results)))))

(defn product->text
  [product]
  (str "Markt: " (get-in product [:outlet :name]) "\n"
       "Produkt: " (:name product) "\n\n"
       "Preis: " (:price product) " €" "\n"
       "\n"
       (:posting_text product) "\n"
       (first (:original_url product))))

(def tgram-url
  (str "https://api.telegram.org/bot" fundgrube-tgram-api-key "/sendMessage"))

(defn format-json-content
  [product]
  (json/encode {"chat_id" fundgrube-tgram-channel
                "text" (product->text product)}))

(defn build-file-structure
  "restructures the json data by extracting the value of a key of the object
   and setting it as new key with object itself as the value.
   
   example, when `:a` is the new key:
   ```clojure
   (build-file-structure :a [{:a \"xxx\", :b 1, :c 2}, {:a \"yyy\", :b 3, :c 4}])
   ;;=> {\"xxx\" {:a \"xxx\", :b 1, :c 2}, \"yyy\" {:a \"yyy\", :b 3, :c 4}}
   ```
   "
  [k json-data]
  (reduce #(assoc %1 (k %2) %2) {} json-data))

(defn send-to-tgram
  [fundgrube-current-results diff-result]
  (let [data (pmap (fn [result]
                     (let [product (get fundgrube-current-results result)]
                       {:headers {"Content-Type" "application/json"}
                        :body (format-json-content product)})) diff-result)]
    (loop [content data]
      (if (first content)
        ((curl/post tgram-url (first content))
         (Thread/sleep 15000)
         (recur (rest content)))
        (str "Sent " (count diff-result) " items.")))))

(defn new-postings-in-fundgrube?
  [fundgrube-current diff-result]
  (and (> (count (keys fundgrube-current)) 0)
       (> (count diff-result) 0)
       (> (count (keys (past-fundgrube-results))) 0)))

(if (fs/exists? filename-current-results)
  (fs/copy filename-current-results filename-past-results {:replace-existing true})
  (spit filename-past-results {}))

(let [json-data (get-json-data url 50 0)
      fundgrube-current (build-file-structure :posting_id json-data)
      diff-result (diff-fundgrube-results fundgrube-current)]
  (pretty-spit filename-current-results fundgrube-current)
  (if (new-postings-in-fundgrube? fundgrube-current diff-result)
    (send-to-tgram fundgrube-current diff-result)
    (prn "job done, nothing sent")))
