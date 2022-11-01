(require '[babashka.curl :as curl])
(require '[clojure.set :as cset])
(require '[clojure.edn :as edn])
(require '[cheshire.core :as json])
(require '[babashka.fs :as fs])

;; adjust to your needs, using the dev tools and inspecting the fundgrube
(def url
  (str "https://www.mediamarkt.de/de/data/fundgrube/api/postings?limit=100&offset=0&outletIds="
       (or (System/getenv "FUNDGRUBE_OUTLET_IDS") "418,576,798")
       "&recentFilter=outlets"))

(def filename-current-results "data_fundgrube.edn")
(def filename-past-results "data_fundgrube_old.edn")

(defn pretty-spit [filename content]
  (spit filename (with-out-str (clojure.pprint/pprint content))))

(defn get-json-data [url]
  (-> (curl/get url {:headers {"Accept" "application/json"
                               "User-Agent" "Bacon/1.0"}})
      (get :body)
      (json/parse-string true)))

(defn diff-fundgrube-results []
  (let [old (edn/read-string (slurp filename-past-results))
        new (edn/read-string (slurp filename-current-results))]
    (cset/difference (set (keys new)) (set (keys old)))))

(defn product->text [product]
  (str "Markt: " (get-in product [:outlet :name]) "\n"
       "Produkt: " (:name product) "\n\n"
       "Preis: " (:price product) " €"
       "\n\n"
       (:posting_text product) "\n"
       (first (:original_url product))))

(defn tgram-url []
  (str "https://api.telegram.org/bot"
       (System/getenv "FUNDGRUBE_TGRAM_API_KEY")
       "/sendMessage"))

(defn json-content [product]
  (json/encode {"chat_id" (System/getenv "FUNDGRUBE_TGRAM_CHANNEL")
                "text" (product->text product)}))

(defn send-to-tgram [fundgrube-current diff-result]
  (for [result diff-result]
    (let [product (get fundgrube-current result)
          url (tgram-url)]
      (curl/post url {:headers {"Content-Type" "application/json"}
                      :body (json-content product)}))))

(defn build-file-structure [json-data]
  (let [fundgrube-current (reduce #(assoc %1 (:name %2) %2) {} (:postings json-data))]
    (pretty-spit filename-current-results fundgrube-current)
    fundgrube-current))

(if (fs/exists? filename-current-results)
  (fs/move filename-current-results filename-past-results {:replace-existing true})
  (spit filename-past-results {}))

(let [json-data (get-json-data url)
      fundgrube-current (build-file-structure json-data)
      diff-result (diff-fundgrube-results)]
  (if (= (count (set (keys fundgrube-current))) (count diff-result))
    (prn "done")
    (send-to-tgram fundgrube-current diff-result)))
