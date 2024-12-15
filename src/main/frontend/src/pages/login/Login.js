
import {useNavigate} from "react-router-dom";
import {useState} from "react";
import "./Login.css";
import Button from '@mui/material/Button';


/**
 *
 * Page to show process login of users
 *
 * */
function Login(props){
    const navi = useNavigate();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    /**
    * Submitting username and password with HTTP POST request after user fills in the information.
    * */
    const onSubmitEvent = (e) => {
        e.preventDefault();
        const dto = {
            username: username,
            password: password,
        };

        fetch("/", {
            method: "POST",
            headers: {
                "Ccontent-Type": "application/json",
            },
            body: JSON.stringify(dto),
        })
            .then((response) => {
                if (!response.ok) {
                    alert("Login failed : wrong id or pw");
                    throw new Error("Failed to login");
                }
                alert("Login successfull");
                navi("/hotels");

                const loginTime = new Date();
                sessionStorage.setItem("loginTime", loginTime);
                sessionStorage.setItem("username", username);

            })
            .catch((error) => {
               console.error("Error during login", error);
            });
    };

    return(
        <div>
            <form className = "login-form" onSubmit = {onSubmitEvent}>
                <div className = "login-form-login">
                    Login
                </div>
                <div className ="login-form-id">
                    ID :
                    <input className = "login-form-id-input" type="text" value={username} onChange={(e) => setUsername(e.target.value)}/>
                </div>
                <div className ="login-form-password">
                    Password:
                    <input className = "login-form-password-input" type="password" value={password} onChange={(e) => setPassword(e.target.value)}/>
                </div>
                <div className ="login-form-loginbtn">
                    <Button variant="contained" className = "login-form-loginbtn-button" type="submit">Login</Button>
                </div>

                <div className = "login-form-register" onClick={()=>navi("/register")} style={{cursor : 'pointer'}}>
                    <Button variant="contained" className = "login-form-register"  style={{cursor : 'pointer'}} onClick={()=>navi("/register")}>Sign-up</Button>
                </div>
            </form>
        </div>
    );
}

export default Login;