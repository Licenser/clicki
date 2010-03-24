(defn layout [uri title & body]
  (api/html [:html [:head [:title (str "Clicki - " title)]] [:body body [:p [:a {:href (str uri "?edit=")} "edit"] " | " [:a {:href "/index"} "home"]]]]))

(layout uri
  "Layout"
  [:p "Implements a layout function."])
