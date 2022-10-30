(require '[babashka.curl :as curl])
(require '[clojure.set :as cset])
(require '[clojure.edn :as edn])
(require '[cheshire.core :as json])
(require '[babashka.fs :as fs])

;; SUPER DUPER CREEPY AND UGLY CODE STUB

(defn pretty-spit [filename content]
  (spit filename (with-out-str (clojure.pprint/pprint content))))

(defn get-json-data [url]
  (-> (curl/get url
                {:headers {"Accept" "application/json"
                           "User-Agent" "Bacon/1.0"}})
      (get :body)
      (json/parse-string true)))

(defn diff-results []
  (let [old (edn/read-string (slurp "data_fundgrube_old.edn"))
        new (edn/read-string (slurp "data_fundgrube.edn"))]
    (cset/difference (set (keys new)) (set (keys old)))))

(if (fs/exists? "data_fundgrube.edn")
  (fs/move "data_fundgrube.edn" "data_fundgrube_old.edn" {:replace-existing true})
  (spit "data_fundgrube_old.edn" {}))

(def url "https://www.mediamarkt.de/de/data/fundgrube/api/postings?limit=100&offset=0&outletIds=418&recentFilter=outlets")

(let [json-data (get-json-data url)]
  ;; persist
  (->> (reduce #(assoc %1 (:name %2) %2) {} (:postings json-data))
       (pretty-spit "data_fundgrube.edn"))
  ;; make diff
  (if (= (count (set (keys (reduce #(assoc %1 (:name %2) %2) {} (:postings json-data)))))
         (count (diff-results)))
    (prn "done")
    (for [result (diff-results)]
      (let [product (get (edn/read-string (slurp "data_fundgrube.edn")) result)]
        ;; TODO: include call to apprise with secret as ENV
        (prn product)))))




