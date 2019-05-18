(ns warframe-lfg.deploy
  (:require [datomic.ion.dev :as id]))

(defn get-deploy-status
  [d]
  (-> {:execution-arn (:execution-arn d)
       :region "us-east-1"}
      id/deploy-status
      :deploy-status))

(defn deploy!
  [uname]
  (let [region        (or (System/getenv "AWS_REGION") "us-east-1")
        config-map    (cond-> {:region region}
                        uname (assoc :uname uname))
        pushed        (id/push config-map)
        group         (-> pushed :deploy-groups first)
        deploy-config (assoc config-map :group group)
        deployed      (id/deploy deploy-config)]
    (loop [status (get-deploy-status deployed)]
      (case status
        "SUCCEEDED" :success
        "RUNNING"   (do (Thread/sleep 1000)
                        (recur (get-deploy-status deployed)))
        (throw (ex-info "Deployment failed!" {:deploy-status status}))))))

(defn -main
  [& [uname]]
  (deploy! uname)
  (shutdown-agents)
  (System/exit 0))
