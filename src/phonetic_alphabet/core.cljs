(ns phonetic-alphabet.core
    (:require
     [reagent.core :as r]
     [reagent.dom :as d]
     [clojure.string :as s]))


;; -------------------------
;; Static data

(def icao 
  {:name "ICAO"
   "a" "Alfa"
   "b" "Bravo"
   "c" "Charlie"
   "d" "Delta"
   "e" "Echo"
   "f" "Foxtrot"
   "g" "Golf"
   "h" "Hotel"
   "i" "India"
   "j" "Juliett"
   "k" "Kilo"
   "l" "Lima"
   "m" "Mike"
   "n" "November"
   "o" "Oscar"
   "p" "Papa"
   "q" "Quebec"
   "r" "Romeo"
   "s" "Sierra"
   "t" "Tango"
   "u" "Uniform"
   "v" "Victor"
   "w" "Whiskey"
   "x" "X-Ray"
   "y" "Yankee"
   "z" "Zulu"}
  )

(def din5009 
  {:name "DIN5009"
   "a" "Aachen"
   (.normalize "ä") "Umlaut Aachen" 
   "b" "Berlin"
   "c" "Chemnitz"
   "d" "Düsseldorf"
   "e" "Essen"
   "f" "Frankfurt"
   "g" "Goslar"
   "h" "Hamburg"
   "i" "Ingelheim"
   "j" "Jena"
   "k" "Köln"
   "l" "Leipzig"
   "m" "München"
   "n" "Nürnberg"
   "o" "Offenbach"
   (.normalize "ö") "Umlaut Offenbach"
   "p" "Potsdam"
   "q" "Quickborn"
   "r" "Rostock"
   "s" "Salzwedel"
   "ẞ" "Eszett"
   "t" "Tübingen"
   "u" "Unna"
   (.normalize "ü") "Umlaut Unna"
   "v" "Völklingen"
   "w" "Wuppertal"
   "x" "Xanten"
   "y" "Ypsilon"
   "z" "Zwickau"}
  )

;; -------------------------
;; Components

(defn capitalize-and-paint 
  [x]
  (if (= x " ")
    [:br]
    [:span [:span {:style {:strong "bold" :color "red"}} (first (s/upper-case x))] (rest x) " "])
  )

(defn input-and-a-field [dictionary]
  (let [value (r/atom "")]
    (fn []
      [:div
       [:input {:type "text" :value @value
                :on-change #(reset! value (-> % .-target .-value))}]
       [:p
        (doall
         (map-indexed
          (fn [i x]
            ^{:key (str x "-" i)}
            [capitalize-and-paint (get @dictionary (.normalize (s/lower-case x)) " ")])
          @value))]])
    )
  )

;; -------------------------
;; Views

(defn home-page []
  (let [dictionary (r/atom icao)]
  (fn []
    [:div
     [:span
      "Phonetic alphabet"
      [:br]
      "Dictionary: " (:name @dictionary)
      [:input {:type "button" :value "Switch"
               :on-click #(swap! dictionary (fn [x] (if (= (:name x) "ICAO") din5009 icao)))}]]
     [input-and-a-field dictionary]])))

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))