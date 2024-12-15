
import {useNavigate} from "react-router-dom";
import {useState} from "react";
import "./Register.css";
import Button from "@mui/material/Button";

/**
 * Page to help users register new id and password.
 * */
function Register(props){

    const navi = useNavigate();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    /**
     * Registering username and password with HTTP POST request after user fills in the information.
     * */
    const onSubmitEvent = (e) => {
        e.preventDefault();
        const dto = {
            username: username,
            password: password,
        };

       fetch("/register", {
           method : "POST",
           headers : {
               "Content-Type": "application/json",
           },
           body:JSON.stringify(dto),
       })
           .then((response) => {
               if(!response.ok){
                 return response.json().then((errorData)=>{
                     alert(errorData.message);
                     throw new Error(errorData.message);
                 });
               }
               alert("Registration successful");
               navi("/");
           })
           .catch((error) => {
               console.error("Error during register", error);
           })
    };


    return(
        <div>
            <form className="register-form" onSubmit={onSubmitEvent}>
                <div className="register-form-register">
                    Register
                </div>

                <div className="register-form-id">
                    ID :
                    <input className="register-form-id-input" type="text" value={username}
                           onChange={(e) => setUsername(e.target.value)}/>
                </div>
                <div className ="register-form-password">
                    Password:
                    <input className="register-form-password-input" type="password" value={password}
                           onChange={(e) => setPassword(e.target.value)}/>
                </div>
                <div>
                    <Button variant="contained" className = "register-form-button" type="submit">Register</Button>
                    <Button variant="contained" className = "register-form-button-cancelbtn" onClick={() => navi("/")}>Back</Button>
                </div>

            </form>
        </div>
    );
}

export default Register;