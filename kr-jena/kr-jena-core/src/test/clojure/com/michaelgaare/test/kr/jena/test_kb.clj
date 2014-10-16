(ns com.michaelgaare.test.kr.jena.test-kb
  (use clojure.test
       com.michaelgaare.kr.kb
       com.michaelgaare.kr.jena.kb)
  (require com.michaelgaare.test.kr.test-kb
           com.michaelgaare.kr.jena.rdf))

;;; --------------------------------------------------------
;;;
;;; --------------------------------------------------------

(defn jena-memory-test-kb []
  ;;(com.michaelgaare.kr.kb/open
  (kb :jena-mem))
;;(com.michaelgaare.kr.jena.kb/new-jena-model-kb));)

(defn test-ns-hook []
  (binding [com.michaelgaare.test.kr.test-kb/*kb-creator-fn*
            jena-memory-test-kb
            com.michaelgaare.kr.jena.rdf/*force-add-named-to-default*
            true]
    (run-tests 'com.michaelgaare.test.kr.test-kb)))



;;; --------------------------------------------------------
;;; END
;;; --------------------------------------------------------
