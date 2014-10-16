(ns com.michaelgaare.test.kr.sesame.test-kb
  (use clojure.test
       com.michaelgaare.kr.kb
       com.michaelgaare.kr.sesame.kb
       com.michaelgaare.kr.sesame.writer-kb)
  (require com.michaelgaare.test.kr.test-kb)
  (import java.io.ByteArrayOutputStream))

;;; --------------------------------------------------------
;;;
;;; --------------------------------------------------------

(defn sesame-memory-test-kb []
  (com.michaelgaare.kr.kb/open
   (com.michaelgaare.kr.sesame.kb/new-sesame-memory-kb)))

(defn test-ns-hook []
  (binding [com.michaelgaare.test.kr.test-kb/*kb-creator-fn*
            sesame-memory-test-kb]
    (run-tests 'com.michaelgaare.test.kr.test-kb)))


;;run the kb tests for the writer-kb too
(defn sesame-writer-test-kb []
  (new-sesame-writer-kb (ByteArrayOutputStream.)))

(defn test-ns-hook []
  (binding [com.michaelgaare.test.kr.test-kb/*kb-creator-fn*
            sesame-writer-test-kb]
    (run-tests 'com.michaelgaare.test.kr.test-kb)))

;;; --------------------------------------------------------
;;; END
;;; --------------------------------------------------------
