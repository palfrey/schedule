(ns herder.helpers
  (:require
   [reagent.core :as r]
   [ajax.core :refer [GET]]
   [clojure.walk :refer [keywordize-keys]]))

(defonce state (r/atom {}))

(defn to-date [tstamp]
  (-> tstamp js/moment (.format "YYYY-MM-DD")))

(defn key-handler [key data]
  (.log js/console (str key) "Response" (pr-str data))
  (swap! state assoc key (keywordize-keys data)))

(defn get-data [key url & {:keys [refresh] :or {refresh false}}]
  (if (and (not refresh) (contains? @state key))
    (key @state)
    (do
      (GET url {:handler (partial key-handler key)})
      {})))

(defn convention-url []
  (str "/api/convention/" (:id @state)))

(defn get-convention [& {:keys [refresh]}]
  (get-data :convention (convention-url) :refresh refresh))

(defn convention-header []
  (let [convention (get-convention)]
    [:nav {:class "navbar navbar-nav navbar-full navbar-dark bg-inverse"}
     [:a {:class "navbar-brand"} (:name convention) " (" (to-date (:from convention)) " - " (to-date (:to convention)) ")"]
     [:ul {:class "nav navbar-nav"}
      [:li {:class "nav-item active"}
       [:a {:class "nav-link"} "Slots"]]
      [:li {:class "nav-item"}
       [:a {:class "nav-link" :href "/"} "Goto convention list"]]]]))
