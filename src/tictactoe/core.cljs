(ns tictactoe.core
  (:require ["react-dom/client" :refer [createRoot]]
            [reagent.core :as r]
            [clojure.string :as str]))

(def board-size 3)
(def tile-count (* board-size board-size))

(def initial-tiles
  (zipmap (range tile-count) (repeat nil)))

(defonce store
  (r/atom {:tiles initial-tiles
           :score {:x 0 :o 0}}))

(defn moves [tiles]
  (->> (vals tiles)
       (filter identity)
       count))

(defn next-player [tiles]
  (if (even? (moves tiles)) :x :o))

(def row-combos
  (partition board-size (keys initial-tiles)))

(def col-combos
  (apply map vector row-combos))

(def diagonal-combos
  [(range 0 tile-count (inc board-size))
   (range (dec board-size) (dec tile-count) (dec board-size))])

(def win-combos
  (concat row-combos col-combos diagonal-combos))

(defn win? [tiles player]
  (some (fn [combo]
          (every? #(= (get tiles %) player) combo))
        win-combos))

(defn tie? [tiles]
  (and (not (win? tiles :x))
       (not (win? tiles :o))
       (= (moves tiles) tile-count)))

(defn make-move [old-state tile-id]
  (let [player (next-player (:tiles old-state))
        new-state (assoc-in old-state [:tiles tile-id] player)]
    (cond-> new-state
      (win? (:tiles new-state) player) (-> (update-in [:score player] inc)
                                           (assoc :tiles initial-tiles)
                                           (assoc :game-over (str (-> player name str/capitalize) " won!")))

      (tie? (:tiles new-state)) (-> (assoc :tiles initial-tiles)
                                    (assoc :game-over "It's a tie!")))))

(defn app []
  (let [{:keys [tiles score game-over]} @store]
    (when game-over
      (js/alert game-over)
      (swap! store dissoc :game-over))
    [:div
     [:div.score.x "X: " (:x score)]
     [:div.score.o "O: " (:o score)]
     [:div.board {:style {:grid-template-columns (str "repeat(" board-size ", 1fr)")}}
      (for [[tile-id move] tiles]
        ^{:key tile-id}
        [:div.square {:class move
                      :on-click
                      (fn [_]
                        (when-not move
                          (swap! store make-move tile-id)))}
         move])]]))

(defonce root (createRoot (js/document.getElementById "app")))

(defn ^:dev/after-load init! []
  (.render root (r/as-element [app])))
