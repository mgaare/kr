(ns com.michaelgaare.test.kr.test-forward-rule
  (use clojure.test
       ;;clojure.test.junit

       com.michaelgaare.test.kr.test-kb

       com.michaelgaare.kr.variable
       com.michaelgaare.kr.kb
       com.michaelgaare.kr.rdf
       com.michaelgaare.kr.sparql
       com.michaelgaare.kr.rule
       com.michaelgaare.kr.forward-rule

       clojure.pprint
       ))

;;; --------------------------------------------------------
;;; constansts
;;; --------------------------------------------------------

(def test-triples-6-1
     '((ex/a  rdf/type        foaf/Person )
       (ex/a  foaf/name       "Alice" )
       (ex/a  foaf/mbox       "<mailto:alice@example.com>" )
       (ex/a  foaf/mbox       "<mailto:alice@work.example>" )

       (ex/b  rdf/type        foaf/Person )
       (ex/b  foaf/name       "Bob" )))

(def test-triples-6-3
     '((ex/a  foaf/name       "Alice" )
       (ex/a  foaf/homepage   "<http://work.example.org/alice/>" )

       (ex/b  foaf/name       "Bob" )
       (ex/b  foaf/mbox       "<mailto:bob@work.example>" )))

(def test-triples-7
     '((ex/a  dc10/title     "SPARQL Query Language Tutorial" )
       (ex/a  dc10/creator   "Alice" )

       (ex/b  dc11/title     "SPARQL Protocol Tutorial" )
       (ex/b  dc11/creator   "Bob" )

       (ex/c  dc10/title     "SPARQL" )
       (ex/c  dc11/title     "SPARQL (updated)" )))

(def test-triples-10-2-1
     '((ex/a    foaf/givenname   "Alice" )
       (ex/a    foaf/family_name "Hacker" )

       (ex/b    foaf/firstname   "Bob" )
       (ex/b    foaf/surname     "Hacker" )))

(def test-triples-numbers-equality
     '((ex/a    foaf/givenname   "Alice" )
       (ex/a    foaf/surname "Hacker" )
       (ex/a    foaf/age          [40 xsd/integer])

       (ex/b    foaf/firstname   "Bob" )
       (ex/b    foaf/surname     "Hacker" )
       (ex/b    foaf/age          40) ;the default should be xsd/integer

       (ex/c    foaf/firstname   "Fred" )
       (ex/c    foaf/surname     "Hacker" )
       (ex/c    foaf/age          [50 xsd/integer])));50)))

(def test-triples-lang
     '((ex/a    foaf/firstname   "Alice" )
       (ex/b    foaf/firstname   ["Bob" "en"])
       (ex/c    foaf/firstname   ["Bob"])))

(def test-triples-md5
  '((ex/a    foaf/firstname   "Alice" )
    (ex/a ex/hasBoss ex/boss1)
    (ex/a ex/atCompany ex/co1)

    (ex/b ex/hasBoss ex/boss1)
    (ex/b ex/atCompany ex/co1)

    (ex/c ex/hasBoss ex/boss2)
    (ex/c ex/atCompany ex/co2)))


(def test-triples-md5-2
  '((ex/a    foaf/firstname   "Alice" )
    (ex/a ex/hasBoss ex/boss1)
    (ex/a ex/atCompany ex/co1)

    (ex/b ex/hasBoss ex/boss2)
    (ex/b ex/atCompany ex/co1)

    (ex/c ex/hasBoss ex/boss2)
    (ex/c ex/atCompany ex/co2)))

;;; --------------------------------------------------------
;;; rules
;;; --------------------------------------------------------

(def rule-1 '{:head ((?/hacker rdf/type ex/Hacker))
              :body ((?/hacker foaf/name ?/name))})

(def rule-2 '{:head ((?/hacker rdf/type    ex/Hacker)
                     (?/hacker ex/controls ?/org)
                     (?/org    rdf/type    ex/HackerOrganization))
              :body ((?/hacker foaf/name ?/name))
              :reify (?/org)
              })

(def rule-3 '{:head ((?/hacker rdf/type    ex/Hacker)
                     (?/hacker ex/controls ?/org)
                     (?/org    rdf/type    ex/HackerOrganization))
              :body ((?/hacker foaf/name ?/name))
              :reify ([?/org {:ln :unique}])
              })

(def rule-4 '{:head ((?/hacker rdf/type    ex/Hacker)
                     (?/hacker ex/controls ?/org)
                     (?/org    rdf/type    ex/HackerOrganization))
              :body ((?/hacker foaf/name ?/name))
              :reify ([?/org {:ln :unique :ns "foaf"}])
              })

;;creates ex/aORG and ex/bORG
(def rule-5 '{:head ((?/hacker rdf/type    ex/Hacker)
                     (?/hacker ex/controls ?/org)
                     (?/org    rdf/type    ex/HackerOrganization))
              :body ((?/hacker foaf/name ?/name))
              :reify ([?/org {:ln (:localname ?/hacker)
                              :ns "ex" :prefix "" :suffix "ORG"}])
              })


(def rule-6 '{:head ((?/hacker rdf/type    ex/Hacker)
                     (?/hacker ex/controls ?/org)
                     (?/org    rdf/type    ex/HackerOrganization))
              :body ((?/hacker foaf/name ?/name))
              :reify ([?/org {:ln (:md5 ?/hacker)
                              :ns "ex" :suffix "_ORG"}])
              })

;;3 people 2 departments
(def rule-7 '{:head ((?/hacker ex/inDept    ?/dept)
                     (?/dept   rdf/type     ex/Department))
              :body ((?/hacker ex/hasBoss   ?/boss)
                     (?/hacker ex/atCompany ?/co))
              :reify ([?/dept {:ln (:md5 ?/boss ?/co)
                               :ns "ex" :prefix "DEPT_"}])
              })

;;bad rule-7 reify var not in head!!
(def bad-rule-7 '{:head ((?/hacker ex/inDept    ?/dept)
                         (?/dept   rdf/type     ex/Department))
                  :body ((?/hacker ex/hasBoss   ?/boss)
                         (?/hacker ex/atCompany ?/co))
                  :reify ([?/org {:ln (:md5 ?/boss ?/co)
                                  :ns "ex" :prefix "DEPT_"}])
                  })


(def rule-8 '{:head ((?/hacker ex/inDept    ?/dept)
                     (?/dept ex/deptID    ?/deptid)
                     (?/dept   rdf/type     ex/Department))
              :body ((?/hacker ex/hasBoss   ?/boss)
                     (?/hacker ex/atCompany ?/co))
              :reify ([?/dept {:ln (:md5 ?/boss ?/co)
                               :ns "ex" :prefix "DEPT_"}]
                      [?/deptid {:ln (:md5 ?/dept ?/co)
                                 :ns "ex" :prefix "DEPT_"}])
              })

(def rule-8-inv '{:head ((?/hacker ex/inDept    ?/dept)
                         (?/dept ex/deptID    ?/deptid)
                         (?/dept   rdf/type     ex/Department))
                  :body ((?/hacker ex/hasBoss   ?/boss)
                         (?/hacker ex/atCompany ?/co))
                  :reify ([?/deptid {:ln (:md5 ?/dept ?/co)
                                     :ns "ex" :prefix "DEPT_"}]
                          [?/dept {:ln (:md5 ?/boss ?/co)
                                   :ns "ex" :prefix "DEPT_"}])
                  })

(def rule-9-optional '{:head ((?/hacker ex/empname   ?/empname))
                       :body ((?/hacker ex/atCompany ?/co)
                              (:optional
                               (?/hacker foaf/firstname ?/empname)))
                       :reify ([?/empname {:ln (:md5 ?/hacker)
                                           :ns "ex" :prefix "DEPT_"}])
                       })

;; this is to test that constants are used in the reifiction
;;  bug https://github.com/drlivingston/kr/issues/7
;;  causing potential collision
(def rule-10 '{:head ((?/hacker ex/inDept    ?/dept)
                      (?/dept   ex/deptID    ?/deptid)
                      (?/dept   rdf/type     ex/Department))
               :body ((?/hacker ex/hasBoss   ?/boss)
                      (?/hacker ex/atCompany ?/co))
               :reify ([?/dept {:ln (:md5 ?/boss ?/co)
                                :ns "ex" :prefix "D_"}]
                       [?/deptid {:ln (:md5 'ex/ID ?/boss ?/co)
                                  :ns "ex" :prefix "D_"}])
               })

;;this rule should throw an exception due to loop in reification dependencies
(def rule-11-exception '{:head ((?/hacker ex/inDept    ?/dept)
                                (?/dept   ex/deptID    ?/deptid)
                                (?/dept   rdf/type     ex/Department))
                         :body ((?/hacker ex/hasBoss   ?/boss)
                                (?/hacker ex/atCompany ?/co))
                         :reify ([?/dept {:ln (:md5 ?/deptid ?/co)
                                          :ns "ex" :prefix "D_"}]
                                 [?/deptid {:ln (:md5 ?/dept ?/co)
                                            :ns "ex" :prefix "D_"}])
                         })


;;; --------------------------------------------------------
;;; tests
;;; --------------------------------------------------------


(deftest test-forward-safe
  (is (forward-safe? rule-1))
  (is (not (forward-safe? rule-2)))
  (doseq [r (list rule-1 rule-2 rule-3 rule-4 rule-5 rule-6 rule-7)]
    (is (forward-safe-with-reification? r)))
  (is (not (forward-safe-with-reification? bad-rule-7)))
  (is (not (all-head-vars-not-in-body-in-reify? bad-rule-7)))
  (is (not (all-reify-vars-in-head? bad-rule-7))))


(kb-test test-forward-1 test-triples-6-3
         (run-forward-rule *kb* *kb* rule-1)
         (is (= 2
                (count
                 (query '((?/person rdf/type ex/Hacker)))))))

(kb-test test-forward-2 test-triples-6-3
         (run-forward-rule *kb* *kb* rule-2)
         (let [results (query '((?/org rdf/type ex/HackerOrganization)))]
           (is (= 2 (count results)))
           (doseq [r results]
             (is (= "ex" (namespace ('?/org r)))))))

(kb-test test-forward-3 test-triples-6-3
         (run-forward-rule *kb* *kb* rule-3)
         (let [results (query '((?/org rdf/type ex/HackerOrganization)))]
           (is (= 2 (count results)))
           (doseq [r results]
             (is (= "ex" (namespace ('?/org r)))))))

(kb-test test-forward-4 test-triples-6-3
         (run-forward-rule *kb* *kb* rule-4)
         (let [results (query '((?/org rdf/type ex/HackerOrganization)))]
           (is (= 2 (count results)))
           (doseq [r results]
             (is (= "foaf" (namespace ('?/org r)))))))

(kb-test test-forward-5 test-triples-6-3
         (run-forward-rule *kb* *kb* rule-5)
         (let [results (query '((?/org rdf/type ex/HackerOrganization)))]
           (is (= 2 (count results)))
           (let [orgs (map (fn [m] (m '?/org)) results)]
           (is (some (set '(ex/aORG)) orgs))
           (is (some '#{ex/bORG} orgs)))))


(kb-test test-forward-6 test-triples-6-3
         (run-forward-rule *kb* *kb* rule-6)
         (let [results (query '((?/org rdf/type ex/HackerOrganization)))]
           (is (= 2 (count results)))
           (let [orgs (map (fn [m] (m '?/org)) results)]
             (doseq [o orgs]
               (is (.endsWith (name o) "_ORG"))))))


(kb-test test-forward-7 test-triples-md5
         (run-forward-rule *kb* *kb* rule-7)
         (let [results (query '((?/org rdf/type ex/Department)))]
           ;;(pprint results)
           (is (= 2 (count results))))
         (let [results (query '((?/person ex/inDept ?/dept)))]
           ;;(pprint results)
           (is (= 3 (count results))))

         (is (= ((first (query '((ex/a ex/inDept ?/dept)))) '?/dept)
                ((first (query '((ex/b ex/inDept ?/dept)))) '?/dept)))
         (is (not (= ((first (query '((ex/a ex/inDept ?/dept)))) '?/dept)
                     ((first (query '((ex/c ex/inDept ?/dept)))) '?/dept))))
         (is (ask '((ex/a ex/inDept ?/dept)
                    (ex/b ex/inDept ?/dept)))))


(kb-test test-forward-8-a test-triples-md5
         (run-forward-rule *kb* *kb* rule-8)
         (let [results (query '((?/org rdf/type ex/Department)))]
           (is (= 2 (count results))))
         (let [results (query '((?/person ex/inDept ?/dept)))]
           (is (= 3 (count results))))

         (is (= ((first (query '((ex/a ex/inDept ?/dept)))) '?/dept)
                ((first (query '((ex/b ex/inDept ?/dept)))) '?/dept)))

         (is (= ((first (query '((ex/a ex/inDept _/dept)
                                 (_/dept ex/deptID ?/deptid))))
                 '?/deptid)
                ((first (query '((ex/b ex/inDept _/dept)
                                 (_/dept ex/deptID ?/deptid))))
                 '?/deptid)))

         (is (not (= ((first (query '((ex/a ex/inDept _/dept)
                                      (_/dept ex/deptID ?/deptid))))
                      '?/deptid)
                     ((first (query '((ex/c ex/inDept _/dept)
                                      (_/dept ex/deptID ?/deptid))))
                      '?/deptid)))))


(kb-test test-forward-8-b test-triples-md5
         (run-forward-rule *kb* *kb* rule-8-inv)
         (let [results (query '((?/org rdf/type ex/Department)))]
           (is (= 2 (count results))))
         (let [results (query '((?/person ex/inDept ?/dept)))]
           (is (= 3 (count results))))

         (is (= ((first (query '((ex/a ex/inDept ?/dept)))) '?/dept)
                ((first (query '((ex/b ex/inDept ?/dept)))) '?/dept)))

         (is (= ((first (query '((ex/a ex/inDept _/dept)
                                 (_/dept ex/deptID ?/deptid))))
                 '?/deptid)
                ((first (query '((ex/b ex/inDept _/dept)
                                 (_/dept ex/deptID ?/deptid))))
                 '?/deptid)))

         (is (not (= ((first (query '((ex/a ex/inDept _/dept)
                                      (_/dept ex/deptID ?/deptid))))
                      '?/deptid)
                     ((first (query '((ex/c ex/inDept _/dept)
                                      (_/dept ex/deptID ?/deptid))))
                      '?/deptid)))))


(kb-test test-forward-8-c test-triples-md5-2
         (run-forward-rule *kb* *kb* rule-8)
         (is (not (= ((first (query '((ex/a ex/inDept ?/dept)))) '?/dept)
                     ((first (query '((ex/b ex/inDept ?/dept)))) '?/dept))))

         (is (not (= ((first (query '((ex/a ex/inDept _/dept)
                                      (_/dept ex/deptID ?/deptid))))
                      '?/deptid)
                     ((first (query '((ex/b ex/inDept _/dept)
                                      (_/dept ex/deptID ?/deptid))))
                      '?/deptid))))

         (is (not (= ((first (query '((ex/a ex/inDept _/dept)
                                      (_/dept ex/deptID ?/deptid))))
                      '?/deptid)
                     ((first (query '((ex/c ex/inDept _/dept)
                                      (_/dept ex/deptID ?/deptid))))
                      '?/deptid)))))

(kb-test test-forward-8-d test-triples-md5-2
         (run-forward-rule *kb* *kb* rule-8-inv)

         ;; (pprint "test-forward-8-d test-triples-md5-2")
         ;; (pprint (query '((ex/a ex/inDept ?/dept))))
         ;; (pprint (query '((ex/b ex/inDept ?/dept))))
         ;; (pprint (query '((ex/a ex/inDept _/dept)
         ;;                  (_/dept ex/deptID ?/deptid))))
         ;; (pprint (query '((ex/b ex/inDept _/dept)
         ;;                  (_/dept ex/deptID ?/deptid))))

         (is (not (= ((first (query '((ex/a ex/inDept ?/dept)))) '?/dept)
                     ((first (query '((ex/b ex/inDept ?/dept)))) '?/dept))))

         (is (not (= ((first (query '((ex/a ex/inDept _/dept)
                                      (_/dept ex/deptID ?/deptid))))
                      '?/deptid)
                     ((first (query '((ex/b ex/inDept _/dept)
                                      (_/dept ex/deptID ?/deptid))))
                      '?/deptid))))

         (is (not (= ((first (query '((ex/a ex/inDept _/dept)
                                      (_/dept ex/deptID ?/deptid))))
                      '?/deptid)
                     ((first (query '((ex/c ex/inDept _/dept)
                                      (_/dept ex/deptID ?/deptid))))
                      '?/deptid)))))


(kb-test test-forward-9-optional test-triples-md5-2
         (run-forward-rule *kb* *kb* rule-9-optional)

         ;; (pprint "test-forward-9-optional test-triples-md5-2")
         ;; (pprint (query '((ex/a ex/empname ?/empname))))
         ;; (pprint (query '((ex/b ex/empname ?/empname))))
         ;; (pprint (query '((ex/c ex/empname ?/empname))))

         (is (= ((first (query '((ex/a foaf/firstname ?/firstname))))
                 '?/firstname)
                ((first (query '((ex/a ex/empname ?/empname))))
                 '?/empname)))

         (is (not (= ((first (query '((ex/a ex/empname ?/empname))))
                      '?/empname)
                     ((first (query '((ex/b ex/empname ?/empname))))
                      '?/empname))))
         (is (not (= ((first (query '((ex/a ex/empname ?/empname))))
                      '?/empname)
                     ((first (query '((ex/c ex/empname ?/empname))))
                      '?/empname))))
         (is (not (= ((first (query '((ex/b ex/empname ?/empname))))
                      '?/empname)
                     ((first (query '((ex/c ex/empname ?/empname))))
                      '?/empname)))))

;;test for collision due to reification potentially not looking at constants
(kb-test test-forward-10 test-triples-md5-2
         (run-forward-rule *kb* *kb* rule-10)

         (is (not (ask '((?/deptid   ex/deptID    ?/deptid))))))

;;this should thorw an exception becuase of a loop in the reification
(kb-test test-forward-10 test-triples-md5-2
         (is (thrown? Exception (run-forward-rule *kb* *kb* rule-11-exception))))


;;; --------------------------------------------------------
;;; END
;;; --------------------------------------------------------
