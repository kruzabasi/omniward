(ns omniward.specs.patient
  (:require
    [clojure.spec.alpha :as s]
    [clojure.spec.gen.alpha :as gen]
    [java-time.api :as jt]))

(def name-pattern #"[A-Za-z ' - .]+")
(def phone-pattern #"[0-9]{10,15}")

(defn lead-0
  "Prepends a leading '0' to a single-digit number if necessary.
   Returns the input number as a string."
  [num]
  (if (> num 9) (str num) (str "0" num)))

(defn range-gen
  "Returns a spec generator for a random int between min and max (inclusive)"
  [min max]
  (s/gen (s/int-in min max)))

(s/def :patient/p-name (s/with-gen
                         (s/and
                          string?
                          #(>= (count %) 2)
                          #(re-matches name-pattern %))

                         #(gen/fmap
                           (fn [[x y]] (str x " " y))
                           (gen/tuple
                            (s/gen #{"Alice" "O'neil" "Habiba" "Mary-Jane" "Eva" "Iris" "Lee"})
                            (s/gen #{"Anna-Mae" "Zara" "McAllister" "Ji-yong" "Jabari" "D'Angelo"})))))

(s/def :patient/gender #{"male" "female" "other"})

(s/def :patient/phone   (s/with-gen
                          #(re-matches phone-pattern %)

                          #(gen/fmap
                            (fn [length]
                              (let [digits (take length (repeatedly (fn [] (rand-int 9))))]
                                (apply str digits)))
                            (range-gen 10 15))))

(s/def :patient/address (s/and string? #(<= 5 (count %) 100)))

(s/def :patient/dob     (s/with-gen
                          #(try
                             (jt/after? (jt/local-date) (jt/local-date %))
                             (catch clojure.lang.ExceptionInfo e
                               (if (= "Conversion failed" (.getMessage e))
                                 (throw
                                  (ex-info
                                   "Invalid date format"
                                   {:cause (ex-message (ex-cause e))}))
                                 (throw e))))

                          #(gen/fmap
                            (fn [[day month year]]
                              (let [day-str   (lead-0 day)
                                    month-str (lead-0 month)]
                                (str year "-" month-str "-" day-str)))
                            (gen/tuple (range-gen 1 28) (range-gen 1 12) (range-gen 1992 2022)))))

(s/def ::patient-data
  (s/keys :req-un [:patient/p-name :patient/gender :patient/dob :patient/phone :patient/address]))

(defn
  create
  [attr]
  (gen/generate (s/gen attr)))