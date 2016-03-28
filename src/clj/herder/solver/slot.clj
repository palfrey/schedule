(in-ns 'herder.solver.types)

(gen-class
 :name herder.solver.types.Slot
 :prefix "slot-"
 :init init
 :state state
 :extends java.lang.Object
 :constructors {[org.joda.time.ReadablePeriod org.joda.time.ReadablePeriod] []}
 :methods [[getSlotsForDay [org.joda.time.DateTime] org.joda.time.Interval]])

(defn- slot-init [start length]
  [[] (ref {:start start :length length})])

(defn- slot-getSlotsForDay [this day]
  (let [beginSlot (t/plus day (getValue this :start))
        endSlot (t/plus beginSlot (getValue this :length))]
    (t/interval beginSlot endSlot)))
