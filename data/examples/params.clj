(c.template/layout uri "Parameter example" 
[:p "This page got the following parameters: " (keys params)
[:ul (map (fn [[k v]] [:li k "->" v]) params)]
[:a {:href (str uri "?p1=1")} "with one parameter"][:br]
[:a {:href (str uri "?p1=1&p2=2")} "with two parameters"][:br]
])