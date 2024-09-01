(ns tictactoe.core
  (:require ["react-dom/client" :refer [createRoot]]
            [reagent.core :as r]))

(def initial-tiles
 (zipmap (range 9) (repeat nil)))

(defonce store
  (r/atom {:tiles initial-tiles
           :score {:x 0 :o 0}}))

(defn moves [tiles]
  (->> (vals tiles)
       (filter identity)
       count))

(defn next-player [tiles]
  (if (even? (moves tiles)) :x :o))

(def win-combos
  [[0 1 2] [3 4 5] [6 7 8]              ; Rows
   [0 3 6] [1 4 7] [2 5 8]              ; Columns
   [0 4 8] [2 4 6]                      ; Diagonals
   ])

(defn win? [tiles player]
  (some (fn [combo]
         (every? #(= (get tiles %) player) combo))
       win-combos))

(defn tie? [tiles]
  (and (not (win? tiles :x))
       (not (win? tiles :o))
       (= (moves tiles) 9)))

(defn app []
  (let [{:keys [tiles]} @store]
    [:div
     [:div
      "State: " @store]
     [:div.score
      "X: " (get-in @store [:score :x])]
     [:div.score
      "O: " (get-in @store [:score :o])]
     [:div.board
      (for [[tile-id move] tiles]
        [:div {:on-click
               (fn [_]
                 (when (not move)
                   (swap! store assoc-in [:tiles tile-id] (next-player tiles))))}
         move])]
     (cond
       (win? tiles :x) (do
                         (swap! store update-in [:score :x] inc)
                         (js/alert "X won!")
                         (swap! store assoc :tiles initial-tiles))

       (win? tiles :o) (do
                         (swap! store update-in [:score :o] inc)
                         (js/alert "O won!")
                         (swap! store assoc :tiles initial-tiles))

       (tie? tiles) (do
                      (js/alert "It's a tie")
                      (swap! store assoc :tiles initial-tiles)))]))


(defonce root (createRoot (js/document.getElementById "app")))

(defn ^:dev/after-load init! []
  (.render root (r/as-element [app])))

(comment
  (win? {0 :x, 1 :o, 2 :o,
         3 :o, 4 :x, 5 :o,
         6 :x, 7 :o, 8 nil}
        :o)

  )
