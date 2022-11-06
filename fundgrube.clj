(require '[cheshire.core :as json])
(require '[babashka.curl :as curl])
(require '[clojure.set :as cset])
(require '[clojure.edn :as edn])
(require '[babashka.fs :as fs])

;; adjust to your needs, using the dev tools and inspecting the fundgrube
(def url "https://www.mediamarkt.de/de/data/fundgrube/api/postings")
(def filename-current-results "data_fundgrube.edn")
(def filename-past-results "data_fundgrube_old.edn")

(defn pretty-spit [filename content]
  (spit filename (with-out-str (clojure.pprint/pprint content))))

(defn get-postings
  [url limit offset]
  (curl/get url  {:headers {"Accept" "application/json"
                            "User-Agent" "Bacon/1.0"}
                  :query-params {"outletIds" (or (System/getenv "FUNDGRUBE_OUTLET_IDS") "418,576,798")
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
  []
  (let [old (edn/read-string (slurp filename-past-results))
        new (edn/read-string (slurp filename-current-results))]
    (cset/difference (set (keys new)) (set (keys old)))))

(defn product->text
  [product]
  (str "Markt: " (get-in product [:outlet :name]) "\n"
       "Produkt: " (:name product) "\n\n"
       "Preis: " (:price product) " €"
       "\n\n"
       (:posting_text product) "\n"
       (first (:original_url product))))

(defn tgram-url
  []
  (str "https://api.telegram.org/bot"
       (System/getenv "FUNDGRUBE_TGRAM_API_KEY")
       "/sendMessage"))

(defn json-content
  [product]
  (json/encode {"chat_id" (System/getenv "FUNDGRUBE_TGRAM_CHANNEL")
                "text" (product->text product)}))

(defn send-to-tgram
  [fundgrube-current diff-result]
  (for [result diff-result]
    (let [product (get fundgrube-current result)
          url (tgram-url)]
      (curl/post url {:headers {"Content-Type" "application/json"}
                      :body (json-content product)}))))

(defn build-file-structure
  [json-data]
  (let [fundgrube-current (reduce #(assoc %1 (:posting_id %2) %2) {} json-data)]
    (pretty-spit filename-current-results fundgrube-current)
    fundgrube-current))

(defn past-fundgrube-results
  []
  (clojure.edn/read-string (slurp filename-past-results)))

(if (fs/exists? filename-current-results)
  (fs/move filename-current-results filename-past-results {:replace-existing true})
  (spit filename-past-results {}))

(let [json-data (get-json-data url 50 0)
      fundgrube-current (build-file-structure json-data)
      diff-result (diff-fundgrube-results)]
  (if (and
       (> (count (keys (past-fundgrube-results))) 0)
       (> (count (keys fundgrube-current)) 0)
       (> (count diff-result) 0))
    (send-to-tgram fundgrube-current diff-result)
    (prn "done")))
