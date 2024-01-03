As a user using the Event Building Inc. application, I would like to perform specific tasks in my event such as hiring staff, decorations,
catering, etc.
You are tasked with creating a sample screen from the Event Building Inc. application that allows the user to select the task they want. All
tasks are categorized under different categories like (Staff, Food, Catering ..etc). As such, the user selects a category and should be able to
see all the items under this category. Each item in the category may be included in multiple categories, and other items will only be included
in one category.



**Features**

* Homepage: List of categories + the estimated budget for the selected categories

* SubCategories: List of subcategories from a Category

* Event Summary: Estimate of budget for the event

**Tech Stack**

* Kotlin Flow
* Retrofit-OkHttp-Gson
* Room
* Jetpack Compose toolkit : ComposeUI, ComposeNavigation, Compose-Coil, Compose-ConstraintLayout, Compose-Tooling, Compose-Material(1 & 3)
* Dependency management: Version Catalog
* Dependency Injection : Dagger-Hilt
* Architecture (presentation layer): MVI


**Project Constraints**

* Given the data provided, we thought it was best to use an offline strategy: data is fetched if and only if it isn't available in our database
* Given the project expectations and the data provided (`GET` requests and data manipulation in the data layer), we did not see the need to use a "pure clean architecture" approach; as such a `data` and `presentation` layers were more than enough to achieve what was required
* The Database: Since the data coming from the server is immutable and would reset local values, we decided to keep a table for the data coming from the server (`TableServer`) and another for the "local" data (`TableLocal`). This way, each row from `TableServer` will be related to a corresponding in `TableLocal`. This will allow us to easily handle UI states
