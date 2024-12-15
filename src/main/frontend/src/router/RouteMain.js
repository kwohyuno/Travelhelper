import { Route, Routes } from "react-router-dom";
import { Login } from "../pages/login";
import { Register } from "../pages/register";
import { Hotels } from "../pages/hotels";
import { Mypage } from "../pages/mypage";

/**
 * RouteMain component: Defines the application's main routing structure using Routes and Route components.
 * */
function RouteMain(props){

    return(
        <Routes>
            <Route>
                <Route path="/" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/hotels" element={<Hotels />} />
                <Route path="/mypage" element={<Mypage />} />
            </Route>
        </Routes>
    );
}
export default RouteMain;



