(ns tictactoe.core
  (:require [reagent.core :as r]
            [reagent.dom :as dom]))

(defn app [store]
  [:div "Test"])

(defn render! [store element]
  (dom/render app element))

(defonce store (r/atom {}))

(defn ^:export init! []
  (render! store
           (js/document.getElementById "app")))
