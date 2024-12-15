import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import "./Mypage.css";
import DeleteIcon from '@mui/icons-material/Delete';
import IconButton from '@mui/material/IconButton';



/**
 *
 * Mypage where user can check last login and favorite hotel and clicked Expedia link.
 *
 * */
function Mypage(props){
    const navi = useNavigate();
    const [favoriteHotelList, setFavoriteHotelList] = useState([]);
    const [expediaLinkList, setExpediaLinkList] = useState([]);
    const [lastLogin, setLastLogin] = useState("");

    /**
     *
     * Default query for needed information - last login, favorite hotel, clicked Expedia link.
     *
     * */
    useEffect(()=>{
        const username = sessionStorage.getItem("username");
        if(!username){
            alert("Login is required to access");
            navi("/");
            return;
        }

        fetch(`/favorite/${username}`, {
            method: "GET",
            headers : {
                "Content-Type": "application/json",
            },
        })
            .then((res) => {
                return res.json();
            })
            .then((data)=>{
                setFavoriteHotelList(data);
                console.log("got the favorite hotel data!");
            })
            .catch((error)=>{
                console.log(error);
            })

        fetch(`/expedia/${username}`, {
            method: "GET",
            headers : {
                "Content-Type": "application/json",
            },
        })
            .then((res) => {
                return res.json();
            })
            .then((data)=>{
                setExpediaLinkList(data);
                console.log(data);
                console.log("got the expedia link data!");
            })
            .catch((error)=>{
                console.log(error);
            })

        fetch(`/loginrecord/${username}`, {
            method: "GET",
            headers : {
                "Content-Type": "application/json",
            },
        })
            .then((res) => {
                return res.json();
            })
            .then((data) => {
                if(data.lastLoginDate === "null"){
                    setLastLogin("You have not logged in before");
                }else{
                    const formattedDate = data.lastLoginDate.split(".")[0];
                    setLastLogin(`Your last login was on: ${formattedDate}`);
                }
            })
            .catch((error)=>{
              console.log(error);
            })
    }, []);


    /**
     *
     * Deleting the history of clicked expedia links.
     *
     * */
    const DeleteExpediaLinks = () =>{
        if(!window.confirm("Delete this review?")){
            return;
        }

        const username = sessionStorage.getItem("username");

        fetch(`/expedia/${username}`, {
            method: "DELETE",
            headers:{
                "Content-Type" : "application/json",
            },
        })
            .then((res)=>{
                if(res.ok){
                    alert("Deleted expedia visited lists");
                    setExpediaLinkList([]);
                }else{
                    alert("Failed to delete expedia visited lists");
                    console.error("Failed to delete expedia visited lists");
                }
            })
            .catch((error)=>{
                console.error("Error deleting expedia visited lists",error);
            });
    };


    /**
     *
     * Deleting the history of saved favorite hotels.
     *
     * */
    const DeleteFavoriteHotels = () => {
        if(!window.confirm("Delete favorite hotel lists?")){
            return;
        }

        const username = sessionStorage.getItem("username");

        fetch(`/favorite/${username}`, {
            method: "DELETE",
            headers:{
                "Content-Type" : "application/json",
            },
        })
            .then((res) =>{
                if (res.ok) {
                    alert("Deleted favorite hotel data");
                    setFavoriteHotelList([]);
                } else {
                    alert("Failed to delete favorite hotel data");
                    console.error("Failed to delete favorite hotel data");
                }
        })
        .catch((error)=>{
            console.log(error);
        });
    };

    /**
     *
     * Moving page back to main page.
     *
     * */
    const back = () => {
        navi("/hotels");
    }

    /**
     *
     * Generating Expedia Link
     *
     * */
    const generateExpediaLink = (hotel) => {

        const formattedName = hotel.hotelName.split(" ").join("-");
        const formattedCity = hotel.city.split(" ").join("-");
        return `https://www.expedia.com/${formattedCity}-Hotels-${formattedName}.h${hotel.hotelId}.Hotel-Information`;
    }

    return(
        <div className="mypage">

            <div className="mypage-header">
                <button className="mypage-header-backbtn" onClick={back}>Main Page</button>
            </div>

            <div className="mypage-body">
                <div className="mypage-body-lastlogin">
                    {lastLogin ? lastLogin : "You have not logged in before"}
                </div>

                <div className="mypage-body-favoritehotel">

                    <h2>List of Favorite Hotels: </h2>
                    <IconButton onClick={DeleteFavoriteHotels} color="error">
                        <DeleteIcon />
                    </IconButton>

                    {Array.isArray(favoriteHotelList) && favoriteHotelList.length > 0 ? (
                        <ul>
                            {favoriteHotelList.map((dto, index) => (
                                <li key={dto.hotelId} className="mypage-body-favoritehotel-item">
                                    <>
                                        <p>Hotel Name : {dto.hotelName}</p>
                                        <p>Link:
                                            <a
                                                href={generateExpediaLink(dto)}
                                                target="_blank"
                                                rel="noopener noreferrer"
                                            >Visit
                                            </a>
                                        </p>
                                    </>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p>No added favorite hotels.</p>
                    )}
                </div>

                <div className="mypage-body-expedialinks">
                    <h2>List of Visited Expedia Links: </h2>
                    <IconButton onClick={DeleteExpediaLinks} color="error">
                        <DeleteIcon />
                    </IconButton>
                    {Array.isArray(expediaLinkList) && expediaLinkList.length > 0 ? (
                        <ul>
                            {expediaLinkList.map((dto, index) => (
                                <li key={dto.link} className="mypage-body-expedialinks-item">
                                    <>
                                        <p>Expedia Link : <a
                                        href={dto.expediaLink}
                                        >{dto.expediaLink}</a></p>
                                    </>

                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p>No added expedia links.</p>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Mypage;