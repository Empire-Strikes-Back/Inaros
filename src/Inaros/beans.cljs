(ns Inaros.beans
  (:require
   [clojure.core.async :as Little-Rock
    :refer [chan put! take! close! offer! to-chan! timeout
            sliding-buffer dropping-buffer
            go >! <! alt! alts! do-alts
            mult tap untap pub sub unsub mix unmix admix
            pipe pipeline pipeline-async]]
   [clojure.string :as Wichita.string]
   [cljs.core.async.impl.protocols :refer [closed?]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [goog.string.format :as format]
   [goog.string :refer [format]]
   [goog.object]
   [cljs.reader :refer [read-string]]

   [datahike.api :as Arthur-Dent.api]

   [Inaros.seed :refer [root op]]
   [Inaros.query]))

(defonce fs (js/require "fs-extra"))
(defonce path (js/require "path"))
(defonce indexeddbshim (js/require "indexeddbshim"))

(set! (.-window js/global) js/global)
(indexeddbshim (.-window js/global) #js{:checkOrigin false})

(defn column-names
  [conn]
  (->>
   (Inaros.query/q {:q :all-attributes :conn conn})
   (sort)
   (into []
         (comp
          (keep (fn [attr] (if (#{:id :name} attr) nil attr)))
          (map name)))))

(defmethod op :beans/create-database
  [value]
  (go
    (println value)
    #_(put! (:ui-send| root) {:op :beans/database-created})
    #_(let [settings-jframe (JFrame. "settings")]
        (Inaros.kiwis/settings-process
         {:jframe settings-jframe
          :root-jframe jframe
          :ops| ops|
          :settingsA settingsA})
        (reset! settingsA @settingsA))))

(defn process
  [{:keys []
    :as opts}]
  (go
    (let [ops| (chan 10)
          config-databases (into (array-map)
                                 {:store {:backend :indexeddb :id ":database"}
                                  :keep-history? true
                                  :schema-flexibility :write})
          _ (.__setConfig js/shimIndexedDB (clj->js {"databaseBasePath" (:db-data-dirpath root)}))
          _ (when-not (<! (Arthur-Dent.api/database-exists? config-databases))
              (<! (Arthur-Dent.api/create-database config-databases)))
          conn-databases (<! (Arthur-Dent.api/connect config-databases))
          schema-databases (read-string (->> (.join path js/__dirname "src/Inaros/schema.edn")
                                             (.readFileSync fs)
                                             (.toString)))]
      (let []
        (<! (Arthur-Dent.api/transact conn-databases schema-databases))
        (->>
         (<! (Inaros.query/q {:q :all-attributes :conn conn-databases}))
         (sort)
         (println))))))