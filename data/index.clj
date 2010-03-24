(c.template/layout (:uri request) "Index" 
  [:ul
    (map 
      (fn [page] [:li [:a {:href page} page]]) (api/list-articles))]
  [:p "To add new pages just enter the path you like as the url."]
  [:div {:id "test"}]
  [:script {:type "text/javascript"} "
var xhr = new XMLHttpRequest(), pages;
xhr.open('GET', 'json/pages', false);
try {
  xhr.send(null);
  pages = JSON.parse(xhr.responseText);
  document.getElementById('test').innerHTML = 'List of pages gotten json:' + pages;
} catch (e) {
  alert('Could not read battle log:' + e);
}
"])
