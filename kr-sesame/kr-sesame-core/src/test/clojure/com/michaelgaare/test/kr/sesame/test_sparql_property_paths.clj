(ns com.michaelgaare.test.kr.sesame.test-sparql-property-paths
  (use clojure.test
       [com.michaelgaare.test.kr.sesame.test-kb :exclude [test-ns-hook]])
  (require com.michaelgaare.test.kr.test-kb
           com.michaelgaare.test.kr.test-sparql-property-paths))

;;; --------------------------------------------------------
;;;
;;; --------------------------------------------------------

(defn test-ns-hook []
  (binding [com.michaelgaare.test.kr.test-kb/*kb-creator-fn*
            sesame-memory-test-kb]
    (run-tests 'com.michaelgaare.test.kr.test-sparql-property-paths)))

;;; --------------------------------------------------------
;;; END
;;; --------------------------------------------------------
