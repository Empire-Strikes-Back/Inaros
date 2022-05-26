(ns Inaros.main
  (:require
   [clojure.core.async :as Little-Rock
    :refer [chan put! take! close! offer! to-chan! timeout
            sliding-buffer dropping-buffer
            go >! <! alt! alts! do-alts
            mult tap untap pub sub unsub mix unmix admix
            pipe pipeline pipeline-async]]
   [clojure.string :as Wichita.string]
   [clojure.pprint :as Wichita.pprint]
   [cljs.core.async.impl.protocols :refer [closed?]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [goog.string.format :as format]
   [goog.string :refer [format]]
   [goog.object]
   [cljs.reader :refer [read-string]]

   [taoensso.timbre :as Sabretooth.core]
   [datahike.api :as Arthur-Dent.api]

   [Inaros.seed :refer [root op]]
   [Inaros.host]
   [Inaros.dates]
   [Inaros.sunflower-seeds]
   [Inaros.apples]
   [Inaros.salt]
   [Inaros.microwaved-onions]
   [Inaros.corn]
   [Inaros.beans]))

(defonce os (js/require "os"))
(defonce fs (js/require "fs-extra"))
(defonce path (js/require "path"))
(defonce express (js/require "express"))
(set! (.-defaultMaxListeners (.-EventEmitter (js/require "events"))) 100)
(set! (.-AbortController js/global) (.-AbortController (js/require "node-abort-controller")))
(defonce OrbitDB (js/require "orbit-db"))
(defonce IPFSHttpClient (js/require "ipfs-http-client"))
(defonce IPFS (js/require "ipfs"))

(Sabretooth.core/merge-config! {:level :info
                                :min-level :info})

(defmethod op :ping
  [value]
  (go
    (Wichita.pprint/pprint value)
    (put! (:ui-send| root) {:op :pong
                            :from :program
                            :meatbuster :Jesus})))

(defmethod op :pong
  [value]
  (go
    (Wichita.pprint/pprint value)))

(defmethod op :game
  [value]
  (go))

(defmethod op :leave
  [value]
  (go))

(defmethod op :discover
  [value]
  (go))

(defmethod op :settings
  [value]
  (go))

(defn ops-process
  [{:keys []
    :as opts}]
  (go
    (loop []
      (when-let [value (<! (:ops| root))]
        (<! (op value))
        (recur)))))

(defn -main []
  (go
    (let []

      (println (format "what i did - i did for all Belters"))
      (println (format "http://localhost:%s" (:port root)))
      (println "i dont want my next job")
      (println "Kuiil has spoken")


      (.ensureDirSync fs (:program-data-dirpath root))
      (.ensureDirSync fs (:db-data-dirpath root))

      (remove-watch (:stateA root) :watch-fn)
      (add-watch (:stateA root) :watch-fn
                 (fn [ref wathc-key old-state new-state]

                   (when (not= old-state new-state))))

      (Inaros.host/process
       {:port (:port root)
        :host| (:host| root)
        :ws-send| (:ui-send| root)
        :ws-recv| (:ops| root)})

      (ops-process {})

      (Inaros.beans/process {})

      #_(let [ipfs #_(<p! (.create IPFS (clj->js
                                         {:repo (.join path (.homedir os) ".Inaros" "ipfs")})))
              (.create IPFSHttpClient "http://127.0.0.1:5001")
              orbitdb (<p!
                       (->
                        (.createInstance
                         OrbitDB ipfs
                         (clj->js
                          {"directory" (.join path (.homedir os) ".Inaros" "orbitdb")}))
                        (.catch (fn [ex]
                                  (println ex)))))]
          (println (.. orbitdb -identity -id))))))


(comment

  (let [ipfs (.create IPFSHttpClient "http://127.0.0.1:5001")
        orbitdb (<p!
                 (->
                  (.createInstance
                   OrbitDB ipfs
                   (clj->js
                    {"directory" (.join path (.homedir os) ".Inaros" "orbitdb")}))
                  (.catch (fn [ex]
                            (println ex)))))]
    (println (.. orbitdb -identity -id)))

  ;
  )