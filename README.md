* The architectural approach you took and why
    MVVM framework was used to follow the observer pattern to better separate of
    concerns and better testing.  Also, since this framework is pushed as best
    practice, other android developers should be able to understand the codebase
    and be able to contribute.

* The trade offs you made and why
    If there are lots of data returned by the api call, utilizing the paging library
    would be option if the the call supports pagination

* How to run your project
    Use the latest android studio version and open the root directory of this project.  The
    default api call to get the stocks will automatically be called once the app launches.
    Also, the overflow menu contains the option to trigger getting an empty list or a malformed
    response.

* 3rd party libraries or copied code you may have used
    - Retrofit and Moshi to make network calls and transform JSON data to POJOs
    - Turbine for Flow testing
    - Mockk for testing
    - Android Jetpack libs with coroutines/Flows for implementing MVVM framework
* Any other information that you would like us to know