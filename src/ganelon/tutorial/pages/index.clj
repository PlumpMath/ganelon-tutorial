(ns ganelon.tutorial.pages.index
  (:require [ganelon.web.dyna-routes :as dyna-routes]
            [ganelon.tutorial.pages.common :as common]
            [noir.cookies :as cookies]
            [ganelon.tutorial.widgets.meetup-add :as meetup-add]
            [ganelon.tutorial.widgets.meetup-edit :as meetup-edit]
            [ganelon.tutorial.middleware :as middleware]))

(defn meetup-layout [& contents]
  (common/layout
    [:div.row-fluid [:div.span3 (meetup-add/new-meetup-widget)
                     ;list here - by admin cookie id
                     ]
     [:div.span1 ]
     [:div#contents.span8 contents]]))

(dyna-routes/defpage "/" []
  (meetup-layout
    [:div.hero-unit [:h1 "Some information"]
     [:p "Basic information about adding meetups provided here."]
     [:p "Please use the form to the left to create or navigate meetups."]]))

(dyna-routes/defpage "/meetup/edit/:id" [id]
  (middleware/with-admin-id-from-meetup! id
    (meetup-layout
      (meetup-edit/meetup-details-widget id))))
