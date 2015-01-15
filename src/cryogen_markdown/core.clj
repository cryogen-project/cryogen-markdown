(ns cryogen-markdown.core
  (:require [cryogen-core.markup :refer [markup-registry rewrite-hrefs]]
            [markdown.core :refer [md-to-html-string]]
            [markdown.transformers :refer [transformer-vector]]
            [clojure.string :as s])
  (:import cryogen_core.markup.Markup))

(defn rewrite-hrefs-transformer
  "A :replacement-transformer for use in markdown.core that will inject the
  given blog prefix in front of local links."
  [{:keys [blog-prefix]} text state]
  [(rewrite-hrefs blog-prefix text) state])

(defn markdown
  "Returns a Markdown (https://daringfireball.net/projects/markdown/)
  implementation of the Markup protocol."
  []
  (reify Markup
    (dir [this] "md")
    (ext [this] ".md")
    (render-fn [this]
      (fn [rdr config]
        (md-to-html-string
          (->> (java.io.BufferedReader. rdr)
            (line-seq)
            (s/join "\n"))
          :reference-links? true
          :heading-anchors true
          :replacement-transformers (conj transformer-vector (partial rewrite-hrefs-transformer config)))))))

(swap! markup-registry conj (markdown))
