(ns com.michaelgaare.test.kr.jena.test-forward-rule
  (use clojure.test
       [com.michaelgaare.test.kr.jena.test-kb :exclude [test-ns-hook]])
  (require com.michaelgaare.test.kr.test-kb
           com.michaelgaare.test.kr.test-forward-rule))

;;; --------------------------------------------------------
;;;
;;; --------------------------------------------------------

(defn test-ns-hook []
  (binding [com.michaelgaare.test.kr.test-kb/*kb-creator-fn*
            jena-memory-test-kb
            com.michaelgaare.kr.jena.rdf/*force-add-named-to-default*
            true]
    ;;these currently fail because there is a reader and writer
    ;;  going in the same model causing a failure...
    ;;  modification during iteration
    ;;(run-tests 'com.michaelgaare.test.kr.test-forward-rule)
    ))

;;; --------------------------------------------------------
;;; END
;;; --------------------------------------------------------
