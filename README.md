# DESCRIPTION OF ONE ACTIVTY BASE APP

Since android update the standard MainActivity app inicializations, it's being a mess to clean a project when you want to start it with only one activity.

This project is a **template** to start your project with one simple and clean activity


---

### Updates on your project to use this as **template**

First off all, remember to update this READ.me file, to match info of your project

#### 1
> Right click on main package name **com.example.base_app** -> refactor -> rename and change the package name for whatever you want

#### 2
> On your **AndroidManifest.xml** under **manifest** tag, change attribute package to your new package name

#### 3
> On res -> values -> strings.xml update your **app_name** string

#### 4
> on **Settings.gradle (Project Settings)** change **rootProject.name**

#### 5
> On **build.gradle(:app)** folow the json keys android > defaultConfig > applicationId and change it to your new package name


