# Petman API
### CSC 4360 - Georgia States University - 2019
____

### Group
+ Alexandre Geraldo
+ Diego Gonzalez
+ Henoch Tedros
+ Royan Hanson
+ Yash Soni

### Application
____
+ Host : Heroku (http://www.heroku.com/)
+ Application URL : https://gsu-petman.herokuapp.com

### Endpoints
___
+ **User**
    + Register an user (/register):
    ```http
    POST https://gsu-petman.herokuapp.com/register HTTP/1.1
    Content-Type: application/json

    {
        "username"   : "petman",
        "password"   : "petman",
        "first_name" : "petman",
        "last_name"  : "petman",
        "email"      : "petman@gsu.edu"
    }
    ```

    + Authenticate (/auth)
    ```http 
    POST https://gsu-petman.herokuapp.com/auth HTTP/1.1
    Content-Type: application/json

    {
        "username" : "petman",
        "password" : "petman"
    }

    # access token returned API
    {
        "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1Ni..."
    }
    ```

    + Get user info (/user/<string:username>)
    ```http
    GET https://gsu-petman.herokuapp.com/user/petman HTTP/1.1
    Content-Type: application/json
    ```

    + Delete user (/user/<string:username>)
    ```http
    DELETE https://gsu-petman.herokuapp.com/user/petman HTTP/1.1
    Content-Type: application/json
    ```

    + Change user password (/user/<string:username>)
    ```http
    PUT https://gsu-petman.herokuapp.com/user/petman HTTP/1.1
    Content-Type: application/json

    {
        "password" : "secret"
    }
    ```

    + List all users (/users) (JWT token required)
    ```http
    GET https://gsu-petman.herokuapp.com/users HTTP/1.1
    Authorization: JWT eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9
    ```

+ **Pet**
    + Add a pet (/pet):
    ```http
    POST https://gsu-petman.herokuapp.com/pet HTTP/1.1
    Content-Type: application/json

    {
        "name"     : "pluto",
        "sex"      : "M",
        "type"     : "dog",
        "breed"    : "bloodhound",
        "owner_id" : 1,
        "bod"      : "1970-01-01"
    }
    ```

    + Get pet info (/pet/<int:pet_id>/<int:owner>)
    ```http
    GET https://gsu-petman.herokuapp.com/pet/2/1 HTTP/1.1
    Content-Type: application/json    
    ```

    + Change pet (/pet/<int:pet_id>/<int:owner>)
    ```http
    PUT https://gsu-petman.herokuapp.com/pet/2/1 HTTP/1.1
    Content-Type: application/json    

    {   
        "sex"   : "Male",
        "type"  : "superdog",
        "breed" : "bloodhound"
    }
    ```

    + Delete pet (/pet/<int:pet_id>/<int:owner>)
    ```http
    DELETE https://gsu-petman.herokuapp.com/pet/2/1 HTTP/1.1
    Content-Type: application/json    
    ```

    + List all pets (/pets/<int:owner>)
    ```http
    GET https://gsu-petman.herokuapp.com/pets/1 HTTP/1.1
    Content-Type: application/json    
    ```
    
+ **Doctor**
    + Add a doctor (/doctor):
    ```http
    POST https://gsu-petman.herokuapp.com/doctor HTTP/1.1
    Content-Type: application/json

    {
        "clinic"   : "Dog Care 2",
        "address"  : "95 Decatur St SE",
        "city"     : "Atlanta",
        "state"    : "GA",
        "zipcode"  : 30303,
        "phone"    : "(999)999-9999",
        "hours"    : "Mon-Fri",
        "contact"  : "Doctor Who",
        "pet_id"   : 1
    }
    ```

    + Get doctor info (/doctor/<int:doctor_id>/<int:pet_id>)
     ```http
    GET https://gsu-petman.herokuapp.com/doctor/2/1 HTTP/1.1
    Content-Type: application/json    
    ```
    + Delete doctor (/doctor/<int:doctor_id>/<int:pet_id>)
     ```http
    DELETE https://gsu-petman.herokuapp.com/doctor/2/1 HTTP/1.1
    Content-Type: application/json    
    ```   

    + List all doctors (/doctors/<int:pet_id>)
     ```http
    GET https://gsu-petman.herokuapp.com/doctors/1 HTTP/1.1
    Content-Type: application/json    
    ```   


+ **Task**
    + Add a task (/task):
    ```http
    POST https://gsu-petman.herokuapp.com/task HTTP/1.1
    Content-Type: application/json

    {
        "description" : "DayCare",
        "due_date"    : "2019-01-01",
        "owner_id"    : 1
    }
    ```

    + Get task info (/task/<int:task_id>/<int:owner>)
    ```http
    GET https://gsu-petman.herokuapp.com/task/1/1 HTTP/1.1
    Content-Type: application/json    
    ```

    + Change task (/task/<int:task_id>/<int:owner>)
    ```http
    PUT https://gsu-petman.herokuapp.com/task/1/1 HTTP/1.1
    Content-Type: application/json    

    {   
        "complete" : true
    }
    ```

    + Delete pet (/task/<int:task_id>/<int:owner>)
    ```http
    DELETE https://gsu-petman.herokuapp.com/task/2/1 HTTP/1.1
    Content-Type: application/json    
    ```

    + List all pets (/tasks/<int:owner>)
    ```http
    GET https://gsu-petman.herokuapp.com/tasks/1 HTTP/1.1
    Content-Type: application/json    
    ```
+ **Advice**
    + Add a advice (/advice):
    ```http
    POST https://gsu-petman.herokuapp.com/advice HTTP/1.1
    Content-Type: application/json

    {
        "description" : "Play with your dog",
        "advice_date" : "2019-01-01",
        "owner_id"    : 1
    }
    ```

    + Get advice info (/advice/<int:advice_id>/<int:owner>)
    ```http
    GET https://gsu-petman.herokuapp.com/advice/1/1 HTTP/1.1
    Content-Type: application/json    
    ```

    + Change task (/advice/<int:advice_id>/<int:owner>)
    ```http
    PUT https://gsu-petman.herokuapp.com/advice/1/1 HTTP/1.1
    Content-Type: application/json    

    {   
        "complete" : true
    }
    ```

    + Delete advice (/advice/<int:advice_id>/<int:owner>)
    ```http
    DELETE https://gsu-petman.herokuapp.com/advice/1/1 HTTP/1.1
    Content-Type: application/json    
    ```

    + List all pets (/advices/<int:owner>)
    ```http
    GET https://gsu-petman.herokuapp.com/advices/1 HTTP/1.1
    Content-Type: application/json    
    ```
