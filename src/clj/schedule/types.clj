(ns schedule.types
  (:import
   [org.optaplanner.core.api.domain.solution PlanningSolution Solution]
   [org.optaplanner.core.api.domain.solution PlanningEntityCollectionProperty]
   [org.optaplanner.core.api.domain.entity PlanningEntity]
   [org.optaplanner.core.api.domain.variable PlanningVariable]
   [org.optaplanner.core.api.domain.valuerange ValueRangeProvider]
   [org.optaplanner.core.api.score.buildin.hardsoft HardSoftScore]

   [schedule HardSoftSolution]

   [java.util ArrayList]
   [org.joda.time ReadablePeriod DateTime]))

(defn- getValue [this k]
  (let [state (.state this)] (.get @state k)))

(defn- setValue [this k v]
  (let [state (.state this)]
    (dosync (alter state assoc k v)) v))

(gen-class 
 :name ^{PlanningSolution {}} schedule.types.ScheduleSolution
 :extends schedule.HardSoftSolution
 :init init
 :state state
 :methods [[^{PlanningEntityCollectionProperty {}} getEvents [] java.util.List]
           [setEvents [java.util.List] void]
           [^{ValueRangeProvider {"id" "slotRange"}} getSlotRange [] java.util.ArrayList]
           [setSlotRange [java.util.ArrayList] void]
           [setFirstDay [org.joda.time.DateTime] void]
           [setLastDay [org.joda.time.DateTime] void]
           [setSlots [java.util.List] void]]
 :prefix "solution-")

(defn- solution-init []
  [[] (ref {:events (ArrayList.)
            :slotRange (ArrayList.)})])

(defn- solution-getEvents [this]
  (ArrayList. (getValue this :events)))

(defn- solution-getProblemFacts [this]
  (getValue this :events))

(defn- solution-getScore [this]
  (getValue this :score))

(defn- solution-setScore [this score]
  (setValue this :score score))

(defn- solution-getSlotRange [this]
  (getValue this :slotRange))

(defn- solution-setSlotRange [this value]
  (setValue this :slotRange value))

(defn- solution-getFirstDay [this]
  (getValue this :firstDay))

(defn- solution-setFirstDay [this value]
  (setValue this :firstDay value))

(defn- solution-setLastDay [this value]
  (setValue this :lastDay value))

(defn- solution-setSlots [this value]
  (setValue this :slots value))

(gen-class 
 :name ^{PlanningEntity {}} schedule.types.Event
 :prefix "event-"
 :init init
 :state state
 :methods [[^{PlanningVariable {"valueRangeProviderRefs" ["slotRange"]}} getSlot [] Object]
           [setSlot [Object] void]])

(defn- event-init []
  [[] (ref {})])

(defn- event-getSlot [this]
  (getValue this :slot))

(defn- event-setSlot [this item]
  (setValue this :slot item))

(gen-class
 :name schedule.types.Slot
 :prefix "slot-"
 :init init
 :state state
 :extends java.lang.Object
 :constructors {[java.lang.Integer org.joda.time.ReadablePeriod] []}
 :methods [])

(defn- slot-init [start length]
  [[] (ref {:start start :length length})])