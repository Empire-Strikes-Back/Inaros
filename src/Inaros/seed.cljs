(ns Inaros.seed
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
   [cljs.reader :refer [read-string]]))

(defonce os (js/require "os"))
(defonce fs (js/require "fs-extra"))
(defonce path (js/require "path"))

(defmulti op :op)

(defonce root (let [program-data-dirpath (or
                                          (some->
                                           (.. js/global.process -env -Inaros_PATH)
                                           (Wichita.string/replace-first  #"~" (.homedir os)))
                                          (.join path (.homedir os) ".Inaros"))]
                {:program-data-dirpath program-data-dirpath
                 :state-file-filepath (.join path program-data-dirpath "Inaros.edn")
                 :db-data-dirpath (.join path program-data-dirpath "Arthur-Dent")
                 :port (or (try (.. js/global.process -env -PORT)
                                (catch js/Error ex nil))
                           3377)
                 :stateA (atom nil)
                 :host| (chan 1)
                 :ops| (chan 10)
                 :ui-send| (chan 10)}))