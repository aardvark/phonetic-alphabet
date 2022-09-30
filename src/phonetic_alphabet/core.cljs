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
   "z" "Zulu"})

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
   "z" "Zwickau"})

;; -------------------------
;; Components

(defn capitalize-and-paint
  [x]
  (if (= x " ")
    [:br]
    [:span.fs-2
     [:span {:style {:strong "bold" :color "red"}} (first (s/upper-case x))]
     (rest x) " "]))

(defn letter->word
  [dictionary l]
  (get (if (= dictionary "ICAO") icao din5009)
       (.normalize (s/lower-case l)) " "))

(defn tts-button [dictionary value voice]
  [:button.btn.btn-outline-primary
   {:on-click (fn [_]
                (let [x (new js/SpeechSynthesisUtterance (map #(letter->word @dictionary %) @value))
                      synth (. js/window -speechSynthesis)
                      _ (set! (. x -voice) @voice)]
                  (.speak synth x)))}
   [:i.bi.bi-play-fill]
   " Press for text to spech"])

(defn input-to-translate
  [value]
  [:input#textToTranslate.form-control
   {:type "text" :value @value
    :on-change #(reset! value (-> % .-target .-value))}])

(defn input-and-a-field [dictionary value]
  (fn []
    [:div
     [:div.form-group
      [:label {:for "textToTranslate"} "Text to translate:"]
      [input-to-translate value]]

     [:div
      [:label "Result"]
      [:div
       (doall
        (map-indexed
         (fn [i x]
           ^{:key (str x "-" i)}
           [capitalize-and-paint ((partial letter->word @dictionary) x)])
         @value))]]
     [:div.row]]))



;; -------------------------
;; Views

(defn home-page []
  (let [dictionary (r/atom "ICAO")
        value (r/atom "HALP?")
        available-voices (group-by #(.-lang %)
                                   (.getVoices (. js/window -speechSynthesis)))
        voice (r/atom (first (.getVoices (. js/window -speechSynthesis))))]
    (fn []
      [:div.container
       [:div.card.border-light
        [:div.card-header.container
         [:h1 "Phonetic alphabet (" @dictionary ")"]]
        
        [:div.card-body

         [:div.form-group
          [:label
           {:for "dictionary"}
           "Dictionary:"]
          [:select#dictionary.form-select
           {:on-change #(reset! dictionary (.-value (.querySelector js/document "#dictionary")))}
           [:option "ICAO"]
           [:option "DIN5009"]]]



         [:div.form-group
          [:label {:for "textToTranslate"} "Text to translate:"]
          [input-to-translate value]]

         [:div
          [:label "Result:"]
          [:div
           (doall
            (map-indexed
             (fn [i x]
               ^{:key (str x "-" i)}
               [capitalize-and-paint ((partial letter->word @dictionary) x)])
             @value))]]
         
         [:hr]

         [tts-button dictionary value voice]
         [:div.form-group
          [:label "Available voices:"]
          [:select#voice-select.form-select
           {:on-change #(reset! voice
                                (first (get available-voices (.-value (.querySelector js/document "#voice-select")))))}
           (map-indexed (fn [idx voice] ^{:key (str "voice-" idx)} [:option voice]) (keys available-voices))]]]]])))

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))