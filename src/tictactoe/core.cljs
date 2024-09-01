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

(defn make-move [old-state tile-id]
  (let [player (next-player (:tiles old-state))
        new-state (assoc-in old-state [:tiles tile-id] player)]
    (cond-> new-state
      (win? (:tiles new-state) player) (-> (update-in [:score player] inc)
                                           (assoc :tiles initial-tiles)
                                           (assoc :game-over (str (name player) " won!")))

      (tie? (:tiles new-state)) (-> (assoc :tiles initial-tiles)
                                    (assoc :game-over "It's a tie!")))))

(defn app []
  (let [{:keys [tiles score game-over]} @store]
    (when game-over
      (js/alert game-over)
      (swap! store dissoc :game-over))
    [:div
     [:div "State: " @store]
     [:div.score "X: " (:x score)]
     [:div.score "O: " (:o score)]
     [:div.board
      (for [[tile-id move] tiles]
        ^{:key tile-id}
        [:div.square {:on-click
                      (fn [_]
                        (when-not move
                          (swap! store make-move tile-id)))}
         move])]]))


(defonce root (createRoot (js/document.getElementById "app")))

(defn ^:dev/after-load init! []
  (.render root (r/as-element [app])))

(comment
  (win? {0 :x, 1 :o, 2 :o,
         3 :o, 4 :x, 5 :o,
         6 :x, 7 :o, 8 nil}
        :o)

  )
