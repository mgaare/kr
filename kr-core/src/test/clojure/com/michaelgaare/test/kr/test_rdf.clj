(ns com.michaelgaare.test.kr.test-rdf
  (use clojure.test
       ;;clojure.test.junit

       com.michaelgaare.test.kr.test-kb

       com.michaelgaare.kr.variable
       com.michaelgaare.kr.kb
       com.michaelgaare.kr.clj-ify
       com.michaelgaare.kr.rdf
       com.michaelgaare.kr.sparql
       )
  (import ;java.io.InputStream
          java.net.URI
          java.io.ByteArrayInputStream)
  )

;;; --------------------------------------------------------
;;; constansts
;;; --------------------------------------------------------

(def ntriple-string
     (str "<http://www.example.org/a> "
          "<http://www.example.org/p> "
          "<http://www.example.org/x> . \n"
          "<http://www.example.org/a> "
          "<http://www.example.org/p> "
          "<http://www.example.org/y> . \n"))

(def uri-a (URI. "http://www.example.org/a"))
(def uri-p (URI. "http://www.example.org/p"))
(def uri-x (URI. "http://www.example.org/x"))

;;; --------------------------------------------------------
;;; tests
;;; --------------------------------------------------------



(kb-test test-kb-with-triples test-triples
         (is *kb*))

(kb-test test-resources nil
         (is (= "ex" (namespace 'ex/a)))
         (is (= "ex" (namespace (clj-ify *kb*
                                         (resource 'ex/a)))))
         (is (= "http://www.example.org/a"
                (sym-to-long-name *kb* 'ex/a)))
         (is (= 'ex/a
                (convert-string-to-sym *kb* "http://www.example.org/a")))
         (binding [*ns-map-to-short* {"http://www.example.org/" "foo"}]
           (is (= 'foo/a
                  (convert-string-to-sym *kb* "http://www.example.org/a")))))





(kb-test load-stream nil
         (load-rdf *kb*
                   ;;.getBytes can take a string argument e.g., "UTF-8"
                   (ByteArrayInputStream. (.getBytes ntriple-string))
                   :ntriple)
         (is (ask-rdf *kb* 'ex/a 'ex/p 'ex/x))
         (is (ask-rdf *kb* 'ex/a 'ex/p 'ex/y))
         (is (not (ask-rdf *kb* 'ex/a 'ex/p 'ex/z))))

(kb-test test-blank-nodes nil
         (is (anon? (bnode *kb*)))
         (is (= "_" (namespace (bnode *kb*)))))

(kb-test test-bnode-retrieval nil
         (is (nil? (add! *kb* 'ex/a 'ex/b '_/c)))
         (is (ask-rdf *kb* 'ex/a 'ex/b nil))
         (is (anon? (nth (first (query-rdf *kb* 'ex/a 'ex/b nil)) 2))))


(kb-test test-add-triple nil
         (is (nil? (add *kb* 'ex/a 'ex/b 'ex/c))))

(kb-test test-multiple-ways-to-add nil
         (let [kb *kb*]
           ;;in parts
           (add kb 'ex/KevinL 'rdf/type 'ex/Person)

           ;;as a triple
           (add kb '(ex/KevinL foaf/name "Kevin Livingston"))

           ;;to the 'default' kb
           (binding [*kb* kb]
             (add '(ex/KevinL foaf/mbox "<mailto:kevin@example.org>")))

           ;;multiple triples
           (add-statements kb
                           '((ex/BobL rdf/type ex/Person)
                             (ex/BobL foaf/name "Bob Livingston")
                             (ex/BobL foaf/mbox "<mailto:bob@example.org>")))))


(kb-test test-one-triple-uri1 nil
         (is (nil? (add! *kb* uri-a 'ex/b 'ex/c)))
         (is (ask-rdf *kb* 'ex/a 'ex/b 'ex/c))
         (is (ask-rdf *kb* uri-a 'ex/b 'ex/c)))

(kb-test test-one-triple-uri2 nil
         (is (nil? (add! *kb* uri-a uri-p uri-x)))
         (is (ask-rdf *kb* 'ex/a 'ex/p 'ex/x))
         (is (ask-rdf *kb* uri-a uri-p uri-x)))


(kb-test test-one-triple nil
         (is (nil? (add! *kb* 'ex/a 'ex/b 'ex/c)))
         (is (ask-rdf *kb* 'ex/a 'ex/b 'ex/c)))

(kb-test test-ask-triple nil
         (is (nil? (add *kb* 'ex/a 'ex/b 'ex/c)))
         (is (ask-rdf *kb* 'ex/a nil nil))
         (is (ask-rdf *kb* nil 'ex/b nil))
         (is (ask-rdf *kb* 'ex/a 'ex/b nil))
         (is (ask-rdf *kb* 'ex/a 'ex/b 'ex/c))
         (is (ask-rdf *kb* nil nil 'ex/c))

         ;;default graph
         (is (ask-rdf *kb* 'ex/a 'ex/b 'ex/c nil))
         (is (ask-rdf *kb* nil 'ex/b 'ex/c nil))
         (is (ask-rdf *kb* nil nil 'ex/c nil))
         (is (ask-rdf *kb* nil 'ex/b nil nil)))

(kb-test test-ask-graph nil
         (is (nil? (add *kb* 'ex/a 'ex/b 'ex/c 'ex/x)))
         (is (ask-rdf *kb* 'ex/a nil nil))
         (is (ask-rdf *kb* nil 'ex/b nil))
         (is (ask-rdf *kb* 'ex/a 'ex/b nil))
         (is (ask-rdf *kb* 'ex/a 'ex/b 'ex/c))
         (is (ask-rdf *kb* nil nil 'ex/c))

         ;;default graph
         (is (ask-rdf *kb* 'ex/a 'ex/b 'ex/c nil))
         (is (ask-rdf *kb* nil 'ex/b 'ex/c nil))
         (is (ask-rdf *kb* nil nil 'ex/c nil))
         (is (ask-rdf *kb* nil 'ex/b nil nil))

         ;;named graph
         (is (ask-rdf *kb* 'ex/a 'ex/b 'ex/c 'ex/x))
         (is (ask-rdf *kb* nil 'ex/b 'ex/c 'ex/x))
         (is (ask-rdf *kb* nil nil 'ex/c 'ex/x))
         (is (ask-rdf *kb* nil 'ex/b nil 'ex/x))

         ;;wrong graph
         (is (not (ask-rdf *kb* 'ex/a 'ex/b 'ex/c 'ex/z)))
         (is (not (ask-rdf *kb* nil 'ex/b 'ex/c 'ex/z)))
         (is (not (ask-rdf *kb* nil nil 'ex/c 'ex/z)))
         (is (not (ask-rdf *kb* nil 'ex/b nil 'ex/z))))


(kb-test test-query-triple nil
         (is (nil? (add *kb* 'ex/a 'ex/b 'ex/c)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a nil nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a 'ex/b nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* nil 'ex/b nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a 'ex/b 'ex/c)))

         ;;quads
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a nil nil nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a 'ex/b nil nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* nil 'ex/b nil nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a 'ex/b 'ex/c nil))))


(kb-test test-query-triple-uri nil
         (is (nil? (add *kb* uri-a 'ex/b 'ex/c)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* uri-a nil nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* uri-a 'ex/b nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* nil 'ex/b nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* uri-a 'ex/b 'ex/c)))

         ;;quads
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a nil nil nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a 'ex/b nil nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* nil 'ex/b nil nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a 'ex/b 'ex/c nil))))


(kb-test test-query-graph nil
         (is (nil? (add *kb* 'ex/a 'ex/b 'ex/c 'ex/x)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a nil nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a 'ex/b nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* nil 'ex/b nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a 'ex/b 'ex/c)))

         ;;default graph
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a nil nil nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a 'ex/b nil nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* nil 'ex/b nil nil)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a 'ex/b 'ex/c nil)))

         ;;named graph
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a nil nil 'ex/x)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a 'ex/b nil 'ex/x)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* nil 'ex/b nil 'ex/x)))
         (is (= '((ex/a ex/b ex/c)) (query-rdf *kb* 'ex/a 'ex/b 'ex/c 'ex/x)))

         ;;wrong graph
         (is (= () (query-rdf *kb* 'ex/a nil nil 'ex/z)))
         (is (= () (query-rdf *kb* 'ex/a 'ex/b nil 'ex/z)))
         (is (= () (query-rdf *kb* nil 'ex/b nil 'ex/z)))
         (is (= () (query-rdf *kb* 'ex/a 'ex/b 'ex/c 'ex/z))))


(kb-test test-add-query-uris nil
         (is (nil? (add *kb*
                        (URI. "http://www.example.org/a")
                        (URI. "http://www.example.org/b")
                        (URI. "http://www.example.org/c"))))
         (is (ask-rdf *kb* 'ex/a 'ex/b 'ex/c))
         (is (ask-rdf *kb*
                      (URI. "http://www.example.org/a")
                      (URI. "http://www.example.org/b")
                      (URI. "http://www.example.org/c"))))

(kb-test test-literal-type-uris nil
         (is (nil? (add *kb*
                        (URI. "http://www.example.org/a")
                        (URI. "http://www.example.org/b")
                        [4 (URI. "http://www.w3.org/2001/XMLSchema#integer")])))
         (is (ask-rdf *kb* 'ex/a 'ex/b 4))
         (is (ask-rdf *kb*
                      (URI. "http://www.example.org/a")
                      (URI. "http://www.example.org/b")
                      [4 (URI. "http://www.w3.org/2001/XMLSchema#integer")])))


(kb-test test-literal-custom-type nil
         (is (nil? (add *kb* 'ex/a 'ex/b ["foo" 'ex/custom])))
         (is (ask-rdf *kb* 'ex/a 'ex/b ["foo" 'ex/custom])))

(kb-test test-literal-custom-type-uris nil
         (is (nil? (add *kb*
                        (URI. "http://www.example.org/a")
                        (URI. "http://www.example.org/b")
                        ["foo" (URI. "http://www.example.org/custom")])))
         (is (ask-rdf *kb* 'ex/a 'ex/b ["foo" 'ex/custom]))
         (is (ask-rdf *kb*
                      (URI. "http://www.example.org/a")
                      (URI. "http://www.example.org/b")
                      ["foo" (URI. "http://www.example.org/custom")])))

;; query returns a list of triples, we're always looking for one
(defn get-literal [s p]
  (nth (first (query-rdf *kb* s p nil))
       2))


(kb-test test-literal-mode nil
         (is (nil? (add *kb*
                        (URI. "http://www.example.org/a")
                        (URI. "http://www.example.org/b")
                        ["foo" (URI. "http://www.example.org/custom")])))
         (is (nil? (add *kb*
                        (URI. "http://www.example.org/c")
                        (URI. "http://www.example.org/d")
                        [4 (URI. "http://www.w3.org/2001/XMLSchema#integer")])))
         (is (nil? (add *kb* 'ex/e 'ex/f [4 'xsd/integer])))
         (is (nil? (add *kb* 'ex/g 'ex/h ["Bob" "en"])))
         (is (nil? (add *kb* 'ex/i 'ex/j ["Bob" ""])))
         (is (nil? (add *kb* 'ex/k 'ex/l 4)))
         (is (nil? (add *kb* 'ex/m 'ex/n ["Bob"])))

         (binding [*literal-mode* nil]
           (is (= "foo" (get-literal 'ex/a 'ex/b)))
           (is (= 4 (get-literal 'ex/c 'ex/d)))
           (is (= 4 (get-literal 'ex/e 'ex/f)))
           (is (= "Bob" (get-literal 'ex/g 'ex/h)))
           (is (= "Bob" (get-literal 'ex/i 'ex/j)))
           (is (= 4 (get-literal 'ex/k 'ex/l)))
           (is (= "Bob" (get-literal 'ex/m 'ex/n))))

         (binding [*literal-mode* :clj]
           (is (= "foo" (get-literal 'ex/a 'ex/b)))
           (is (= 4 (get-literal 'ex/c 'ex/d)))
           (is (= 4 (get-literal 'ex/e 'ex/f)))
           (is (= "Bob" (get-literal 'ex/g 'ex/h)))
           (is (= "Bob" (get-literal 'ex/i 'ex/j)))
           (is (= 4 (get-literal 'ex/k 'ex/l)))
           (is (= "Bob" (get-literal 'ex/m 'ex/n))))


         (binding [*literal-mode* :clj-type]
           (is (= ["foo" 'ex/custom] (get-literal 'ex/a 'ex/b)))
           (is (= [4 'xsd/integer] (get-literal 'ex/c 'ex/d)))
           (is (= [4 'xsd/integer] (get-literal 'ex/e 'ex/f)))
           (is (= ["Bob" "en"] (get-literal 'ex/g 'ex/h)))
           ;;(is (= ["Bob" ""] (get-literal 'ex/i 'ex/j)))
           (is (= ["Bob" nil] (get-literal 'ex/i 'ex/j)))
           (is (= [4 'xsd/integer] (get-literal 'ex/k 'ex/l)))
           (is (= ["Bob" nil] (get-literal 'ex/m 'ex/n))))


         (binding [*literal-mode* :string]
           (is (= ["foo" 'ex/custom] (get-literal 'ex/a 'ex/b)))
           (is (= ["4" 'xsd/integer] (get-literal 'ex/c 'ex/d)))
           (is (= ["4" 'xsd/integer] (get-literal 'ex/e 'ex/f)))
           (is (= ["Bob" "en"] (get-literal 'ex/g 'ex/h)))
           ;;(is (= ["Bob" ""] (get-literal 'ex/i 'ex/j)))
           (is (= ["Bob" nil] (get-literal 'ex/i 'ex/j)))
           (is (= ["4" 'xsd/integer] (get-literal 'ex/k 'ex/l)))
           (is (= ["Bob" nil] (get-literal 'ex/m 'ex/n))))


         (binding [*literal-mode* (fn [lit type-or-lang]
                                    (if (= type-or-lang 'ex/custom)
                                         :clj-type
                                         nil))]
           (is (= ["foo" 'ex/custom] (get-literal 'ex/a 'ex/b)))
           (is (= 4 (get-literal 'ex/c 'ex/d)))
           (is (= 4 (get-literal 'ex/e 'ex/f)))
           (is (= "Bob" (get-literal 'ex/g 'ex/h)))
           (is (= "Bob" (get-literal 'ex/i 'ex/j)))
           (is (= 4  (get-literal 'ex/k 'ex/l)))
           (is (= "Bob" (get-literal 'ex/m 'ex/n))))


         (binding [*literal-mode* (fn [lit type-or-lang]
                                    (if (string? type-or-lang)
                                         :clj-type
                                         nil))]
           (is (= "foo" (get-literal 'ex/a 'ex/b)))
           (is (= 4 (get-literal 'ex/c 'ex/d)))
           (is (= 4 (get-literal 'ex/e 'ex/f)))
           (is (= ["Bob" "en"] (get-literal 'ex/g 'ex/h)))
           ;;(is (= ["Bob" ""] (get-literal 'ex/i 'ex/j)))
           (is (= "Bob" (get-literal 'ex/i 'ex/j)))
           (is (= 4 (get-literal 'ex/k 'ex/l)))
           (is (= "Bob" (get-literal 'ex/m 'ex/n))))

)





;;; --------------------------------------------------------
;;; END
;;; --------------------------------------------------------
