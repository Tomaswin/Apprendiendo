<?php
$router = new Router;
// How GET requests will be defined
$router->get('/some/route', function($request) {
    // The $request argument of the callback
    // will contain information about the request
    return "Content";
});
// How POST requests will be defined
$router->post('/some/route', function($request) {
    // How to get data from request body
    $body = $request->getBody();
});

class Router
{
  private $request;
  private $supportedHttpMethods = array(
    "GET",
    "POST"
  );
  function __construct(IRequest $request)
  {
   $this->request = $request;
  }
  function __call($name, $args)
  {
    list($route, $method) = $args;
    if(!in_array(strtoupper($name), $this->supportedHttpMethods))
    {
      $this->invalidMethodHandler();
    }
    $this->{strtolower($name)}[$this->formatRoute($route)] = $method;
  }
  /**
   * Removes trailing forward slashes from the right of the route.
   * @param route (string)
   */
  private function formatRoute($route)
  {
    $result = rtrim($route, '/');
    if ($result === '')
    {
      return '/';
    }
    return $result;
  }
  private function invalidMethodHandler()
  {
    header("{$this->request->serverProtocol} 405 Method Not Allowed");
  }
  private function defaultRequestHandler()
  {
    header("{$this->request->serverProtocol} 404 Not Found");
  }
  /**
   * Resolves a route
   */
  function resolve()
  {
    $methodDictionary = $this->{strtolower($this->request->requestMethod)};
    $formatedRoute = $this->formatRoute($this->request->requestUri);
    $method = $methodDictionary[$formatedRoute];
    if(is_null($method))
    {
      $this->defaultRequestHandler();
      return;
    }
    echo call_user_func_array($method, array($this->request));
  }
  function __destruct()
  {
    $this->resolve();
  }
}
 ?>
