const API_URL = "http://172.17.0.2:9999/api/";

//User Login Function
function userLogin(email, password) {
    document.getElementById("loader").classList.remove("hidden");
    $.ajax({
        type: "POST",
        url: API_URL + "user/login",
        data: JSON.stringify({
            "email": email,
            "password": password
        }),
        contentType: "application/json",
        success: function(result){
            if(result.code == "200"){
                sessionStorage.setItem("loggedUser", JSON.stringify({
                    "loginId": JSON.parse(result.data).loginId,
                    "userId": JSON.parse(result.data).userId,
                    "loginRole": JSON.parse(result.data).loginRole,
                    "firstName": JSON.parse(result.data).firstName,
                    "authString": JSON.parse(result.data).authString,
                    "loginCount": JSON.parse(result.data).loginCount
                }));
                window.location.href = "home.html";
            }else{
                document.getElementById("loader").classList.add("hidden");
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
            "phoneNo": phoneNumber,
            "gender": gender,
            "address": address,
            "email": email,
            "password": password
        }),
        contentType: "application/json",
        success: function(result){
            console.log(result);
            if(result.code == "200"){
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
