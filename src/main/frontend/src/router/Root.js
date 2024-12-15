import React from "react";
import {BrowserRouter} from "react-router-dom";
import RouteMain from "./RouteMain";


/**
 * Root component: The top-level component that initializes routing for the application using BrowserRouter.
 * */
function Root(props){

    return (
        <BrowserRouter>
            <RouteMain/>
        </BrowserRouter>
    );
}

export default Root;