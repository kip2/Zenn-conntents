(require '[clojure.string :as str])

(defn split-sections [text]
  (let [lines (str/split-lines text)
        start-index (->> lines
                         (map-indexed vector)
                         (filter #(str/starts-with? (second %) "## "))
                         (first)
                         (first))
        header (str/join "\n" (take start-index lines))
        content-lines (drop start-index lines)]
    {:header header
     :sections
     (->> content-lines
          (partition-by #(str/starts-with? % "## "))
          (partition 2)
          (map #(str/join "\n" (flatten %))))}))

(defn sort-sections [sections]
  (sort-by #(second (re-find #"^##\s*(.*)" %)) sections))

(defn read-sort-and-write-sections [input-file output-file]
  "ファイルを読み込み、セクションをソートし、メタデータとともに出力ファイルに書き込む"
  (let [{:keys [header sections]} (split-sections (slurp input-file))
        sorted-sections (sort-sections sections)
        final-output (str header "\n\n" (str/join "\n\n" sorted-sections))]
    (spit output-file final-output)
    (println (str "Sorted sections written to " output-file))))

;; 実行例
(read-sort-and-write-sections "./articles/clojure-function-cheatsheet.md" "./articles/output.md")

