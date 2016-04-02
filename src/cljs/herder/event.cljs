(ns herder.event
  (:require
   [herder.helpers :refer [get-data state convention-url convention-header nav!]]
   [reagent.core :as r]
   [herder.persons :refer [get-persons person-url]]
   [herder.events :refer [get-events]]
   [ajax.core :refer [PATCH DELETE]]))

(defn event-url [& {:keys [event_id] :or {event_id (:event_id @state)}}]
  (str (convention-url) "/event/" event_id))

(defn get-event [& {:keys [event_id refresh] :or {event_id (:event_id @state)}}]
  (get-data (keyword (str "event_" event_id)) (event-url :event_id event_id) :refresh refresh))

(defn get-person [id]
  (merge {:id id}
         (get-data (keyword (str "person_" id)) (person-url id))))

(defn add-new [val]
  (js/console.log (pr-str @val))
  (PATCH (event-url)
    {:params {:person (:person @val)}
     :format :json
     :handler
     (fn [resp]
       (get-event :refresh true))}))

(defn ^:export component []
  (let [val (r/atom {:person ""})]
    (fn []
      (let [event (get-event)]
        [:div {:class "container-fluid"}
         [convention-header :events]
         [:h2 "Event: " (:name event)]
         [:hr]
         [:h4 "People"]
         (into [:ul]
               (for [{:keys [id name]} (map get-person (:persons event))]
                 ^{:key id} [:li name " "
                             [:button {:type "button"
                                       :class "btn btn-danger"
                                       :on-click #(DELETE (str (event-url) "/person/" id)
                                                    {:handler
                                                     (fn [resp] (get-event :refresh true))})}
                              (str "Remove " name)]]))
         [:form {:class "form-inline"
                 :on-submit #(do
                               (.preventDefault %)
                               (add-new val)
                               false)}
          [:div {:class "form-group"}
           [:label {:for "person"} "Add "]

           [:select {:id "person"
                     :value (:person @val)
                     :on-change #(swap! val assoc :person (-> % .-target .-value))}
            [:option {:value ""} " Select "]
            (for [{:keys [id name]} (get-persons)]
              ^{:key id} [:option {:value id} name])]

           [:button {:type "button"
                     :class "btn btn-primary"
                     :style {:margin-left "5px"}
                     :disabled (= "" (:person @val))
                     :on-click #(add-new val)}
            "Add person"]]]
         [:hr]
         [:button {:type "button"
                   :class "btn btn-danger"
                   :on-click #(DELETE (event-url)
                                {:handler
                                 (fn [resp]
                                   (get-events :refresh true)
                                   (nav! "/events"))})}
          "Delete this event"]]))))