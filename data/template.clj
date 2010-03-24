(defn layout-with-head [uri head & body]
  (api/html [:html [:head head] [:body body [:hr] [:p [:a {:href (str uri "?edit=")} "edit"] " | " [:a {:href "/index"} "home"]]]]))

(defn layout [uri title & body]
  (layout-with-head uri [:title (str "Clicki - " title)] body))

(layout uri
  "Layout"
  [:p "Implements a layout function."])
