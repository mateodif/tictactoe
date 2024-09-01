(ns tictactoe.core-test
  (:require [tictactoe.core :as sut]
            [cljs.test :refer-macros [deftest is testing run-all-tests]]))

(deftest next-player-test
  (testing "next-player function"
    (is (= :x (sut/next-player sut/initial-tiles)))
    (is (= :o (sut/next-player {0 :x, 1 nil, 2 nil})))
    (is (= :x (sut/next-player {0 :x, 1 :o, 2 nil})))))

(deftest win-test
  (testing "win? function"
    (is (sut/win? {0 :x 1 :x 2 :x
                   3 nil 4 nil 5 nil
                   6 nil 7 nil 8 nil}
                  :x))
    (is (sut/win? {0 :o  3 :o  6 :o
                   1 nil 2 nil 4 nil
                   5 nil 7 nil 8 nil}
                  :o))
    (is (not (sut/win? {0 :x  1 :o  2 nil
                        3 nil 4 :x  5 nil
                        6 nil 7 nil 8 nil}
                       :x)))
    (is (not (sut/win? sut/initial-tiles :x)))))

(deftest tie-test
  (testing "tie? function"
    (is (sut/tie? {0 :x 1 :o 2 :x
                   3 :o 4 :x 5 :x
                   6 :o 7 :x 8 :o}))
    (is (not (sut/tie? {0 :x 1 :o 2 :x
                        3 :o 4 :x 5 :x
                        6 :o 7 :x 8 nil})))
    (is (not (sut/tie? {0 :x 1 :x 2 :x
                        3 :o 4 :o 5 nil
                        6 nil 7 nil 8 nil})))))

(deftest full-game-test
  (testing "full game simulation"
    (let [game-state (-> {:tiles sut/initial-tiles :score {:x 0 :o 0}}
                         (sut/make-move 0)   ; X
                         (sut/make-move 4)   ; O
                         (sut/make-move 1)   ; X
                         (sut/make-move 3)   ; O
                         (sut/make-move 2))] ; X
      (is (= "x won!" (:game-over game-state)))
      (is (= {:x 1 :o 0} (:score game-state)))
      (is (= sut/initial-tiles (:tiles game-state))))))

(comment
  (run-all-tests)
  )
