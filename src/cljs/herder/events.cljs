(ns herder.events
  (:require
   [herder.helpers :refer [get-data state convention-url convention-header]]
   [reagent.core :as r]
   [ajax.core :refer [POST DELETE]]))

(defn events-url []
  (str (convention-url) "/event"))

(defn get-events [& {:keys [refresh]}]
  (get-data :events (events-url) :refresh refresh))

(defn create-new [val]
  (POST (events-url)
    {:params @val
     :format :json
     :handler
     (fn [resp]
       (do
         (reset! val {})
         (get-events :refresh true)))}))

(defn ^:export component []
  (let [val (r/atom {})]
    (fn []
      [:div {:class "container-fluid"}
       [convention-header :events]
       [:h2 "Events"]
       [:ul
        (for [{:keys [id name]} (get-events)]
          ^{:key id} [:li [:a {:href (str "#/events/" id)} name " "]])]
       [:hr]
       [:form {:class "form-inline"
               :on-submit #(do
                             (.preventDefault %)
                             (create-new val)
                             false)}
        [:div {:class "form-group"}
         [:label {:for "name"} "Add Event with name"]
         [:input {:id "name"
                  :name "name"
                  :type "text"
                  :placeholder "Name"
                  :class "form-control input-md"
                  :value (:name @val)
                  :on-change #(swap! val assoc :name (-> % .-target .-value))}]
         [:button {:type "button"
                   :class "btn btn-primary"
                   :style {:margin-left "5px"}
                   :on-click #(create-new val)}
          "Create a new event"]]]])))
