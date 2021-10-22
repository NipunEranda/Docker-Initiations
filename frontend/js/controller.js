const API_URL = "localhost:9999/api/";

//User Login Function
function userLogin(email, password) {
    $.ajax({
        type: "POST",
        url: API_URL + "user/login",
        data: JSON.stringify({
            "email": email,
            "password": password
        }),
        contentType: "application/json",
        success: function(result){
            console.log(result);
            if(result.status == "success"){
                sessionStorage.setItem("loggedUser", JSON.stringify({
                    "loginId": result.loginId,
                    "userId": result.userId,
                    "loginRole": result.loginRole,
                    "firstName": result.firstName,
                    "authString": result.authString,
                    "loginCount": result.loginCount
                }));
                window.location.href = "home.html";
            }else{
                window.location.href = "index.html";
            }
        },
        error: function(result, status){
            console.log(result);
        }
    });
}

//User Registration Function
function userRegistration(firstName, lastName, initials, dob, phoneNumber, gender, address, email, password){
    $.ajax({
        type: "POST",
        url: API_URL + "user/register",
        data: JSON.stringify({
            "firstName": firstName,
            "lastName": lastName,
            "initials": initials,
            "dob": dob,
            "phoneNumber": phoneNumber,
            "gender": gender,
            "address": address,
            "email": email,
            "password": password
        }),
        contentType: "application/json",
        success: function(result){
            console.log(result);
            if(result.status == "success"){
                window.location.href = "index.html";
            }else{
                window.location.href = "register.html";
            }
        },
        error: function(result, status){
            console.log(result);
        }
    });
}

//Get User Details
function getUserDetails(id){
    $.ajax({
        type: "GET",
        url: API_URL + "user/" + id,
        success: function(result){
            console.log(result);
        },
        error: function(result, status){
            console.log(result);
        }
    });
}
