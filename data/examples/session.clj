(let [uri (:uri request)]
  (if (-> request :params :session-value)
    [
    {:session (assoc (:session request) :session-value (-> request :params :session-value))}
    {:body (c.template/layout 
      uri 
      "Session Example" 
      [:p "Your last session was set to: " (-> request :session :session-value)]
      [:p "Your session was set to: " (-> request :params :session-value)]
      [:p 
        "Set your session value to " 
        [:a {:href (str uri "?session-value=42")} 42] 
        " or to "
        [:a {:href (str uri "?session-value=42")} 23] "."])}
    ]
    (c.template/layout 
      uri 
      "Session Example" 
      [:p "Your session is set to: " (-> request :session :session-value)]
      [:p 
        "Set your session value to " 
        [:a {:href (str uri "?session-value=42")} 42] 
        " or to "
        [:a {:href (str uri "?session-value=42")} 23] "."])))