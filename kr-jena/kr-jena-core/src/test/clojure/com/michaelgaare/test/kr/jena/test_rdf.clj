(ns com.michaelgaare.test.kr.jena.test-rdf
  (use clojure.test
       [com.michaelgaare.test.kr.jena.test-kb :exclude [test-ns-hook]])
  (require com.michaelgaare.test.kr.test-kb
           com.michaelgaare.test.kr.test-rdf))

;;; --------------------------------------------------------
;;;
;;; --------------------------------------------------------

(defn test-ns-hook []
  (binding [com.michaelgaare.test.kr.test-kb/*kb-creator-fn*
            jena-memory-test-kb
            com.michaelgaare.kr.jena.rdf/*force-add-named-to-default*
            true]
    (run-tests 'com.michaelgaare.test.kr.test-rdf)))

;;; --------------------------------------------------------
;;; END
;;; --------------------------------------------------------
