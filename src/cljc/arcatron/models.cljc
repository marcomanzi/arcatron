(ns arcatron.models
  #?(:clj
     (:import
       [java.util UUID Date]
       [java.text SimpleDateFormat])))

(def names ["Marco" "Valerio" "Barbara" "Mirko" "Vincenzo" "Agnese" "Assunta" "Enrico" "Buffy" "Giles" "Willow"
            "Giles" "Vladimir" "Maria" "Salvatore"])

(def surnames ["Rossi" "Manzi" "Tinaglia" "Summer" "Vacant" "Pasqualino" "Raiola" "Scognamiglio" "Sampler"])

(defn random-in-list [l]
  (nth l (rand-int (count l))))

(defn random-name []
  (random-in-list names))

(defn random-surname []
  (random-in-list surnames))

(defn rand-int-between [s e]
  (+ s (rand-int (- e s))))

(defn random-a-to-z-char []
  (nth (map char (range 65 91)) (rand-int 21)))

(defn random-fc-generator []
  (let [rc random-a-to-z-char
        rn #(+ 1 (rand-int 9))]
    (str (rc) (rc) (rc) (rc) (rc) (rc) (rn) (rn) (rc) (rn) (rn) (rc) (rn) (rn) (rn) (rc))))

(defn random-phone-numbers-generator []
  (let [rn #(+ 1 (rand-int 9))]
    (reduce str (map (fn [x] (rn)) (range 0 9)))))

(def rand-boolean #(= 1 (rand-int 2)))

(defrecord Customer [uuid name surname fiscal_code phone_number invoices_payed profit_margin address city])

(defn big-rand-int [] (rand-int 100000))

(defn random-uuid []
  #?(:clj (UUID/randomUUID)
     :cljs (cljs.core/random-uuid)))

(defn generate-customer []
  (map->Customer {:uuid (random-uuid)
                  :name (random-name)
                  :surname (random-surname)
                  :fiscal_code (random-fc-generator)
                  :phone_number (random-phone-numbers-generator)
                  :invoices_payed (rand-boolean)}))

(defn empty-customer []
  (map->Customer {:uuid nil}))

(defrecord Price [uuid destination prefix price_per_minute created_on])

(defn generate-price [destination prefix price_per_minute]
  #?(:clj (->Price (random-uuid) destination prefix price_per_minute (Date.)))
  #?(:cljs (->Price (random-uuid) destination prefix price_per_minute nil)))

(defn empty-price []
  (map->Price {:uuid nil :created_on (Date.)}))

(defrecord ^{:private true } CallDataRecord [uuid time_stamp record_sequence_number call_duration caller receiver nv cid id_service caller_uuid price_uuid errors])

(defn ->call-data-record [values]
  (let [base-cdr (apply ->CallDataRecord values)]
    (if-let [errors-str (:errors base-cdr)]
      (assoc base-cdr :errors (read-string errors-str))
      (assoc base-cdr :errors []))))

(defn map->call-data-record [map-values]
  (let [base-cdr (map->CallDataRecord map-values)]
    (if-let [errors-str (:errors base-cdr)]
      (assoc base-cdr :errors (read-string errors-str))
      (assoc base-cdr :errors []))))

(defn to-date
  "Convert the string in a date 15/03/2016 15:51:18"
  [s]
  (if (nil? s)
    s
    (.parse (SimpleDateFormat. "dd/MM/yyyy hh:mm:ss") s)))

(defn non-evaluated-call-data-record [cdr-values]
  (let [clean-values (map clojure.string/trim cdr-values)
        cdr-with-all-strings (->call-data-record (concat [(random-uuid)] clean-values [nil nil nil]))
        cdr-sanitized (-> cdr-with-all-strings
                          (assoc :time_stamp (to-date (:time_stamp cdr-with-all-strings)))
                          (assoc :call_duration (Integer/parseInt (:call_duration cdr-with-all-strings))))]
    cdr-sanitized))
