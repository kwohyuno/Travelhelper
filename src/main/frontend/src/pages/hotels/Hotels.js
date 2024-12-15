import {useNavigate} from "react-router-dom";
import {useEffect, useState} from 'react';
import "./Hotels.css";
import {v4 as uuidv4} from 'uuid';
import {MapContainer, Marker, TileLayer, Popup, useMap} from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import * as L from 'leaflet';

/**
*
* Page to show hotel information and can read, write, edit, and delete reviews
*
* */
function Hotels(props){
    const navi = useNavigate();
    const [hotelList, setHotelList] = useState([]);
    const [filteredHotelList, setFilteredHotelList] = useState([]);
    const [filteredLocations, setFilteredLocations] = useState([]);
    const [searchInput, setSearchInput] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedHotelReviews, setSelectedHotelReviews] = useState([]);
    const [selectedHotelName, setSelectedHotelName] = useState("");
    const [selectedHotelId, setSelectedHotelId] = useState("");
    const [selectedHotelAddress, setSelectedHotelAddress] = useState("");
    const [selectedHotelCity, setSelectedHotelCity] = useState("");
    const [selectedHotelLatitude, setSelectedHotelLatitude] = useState("");
    const [selectedHotelLongitude, setSelectedHotelLongitude] = useState("");
    const [newReviewTitle, setNewReviewTitle] = useState("");
    const [newReviewRating, setNewReviewRating] = useState("");
    const [newReviewText, setNewReviewText] = useState("");
    const [calculatedAverageRating, setCalculatedAverageRating] = useState("");
    const [editIndex, setEditIndex] = useState(null);
    const [newEditReviewTitle, setNewEditReviewTitle] = useState("");
    const [newEditReviewRating, setNewEditReviewRating] = useState("");
    const [newEditReviewText, setNewEditReviewText] = useState("");
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 5;
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const currentReviews = selectedHotelReviews?.slice(startIndex, endIndex)||[];
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState("");

    /**
     * Adjusting map feature (icon in map)
     * */
    const DefaultIcon = L.icon({
       iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
       iconUrl: require('leaflet/dist/images/marker-icon.png'),
       shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
       iconSize: [24,51],
       iconAnchor: [12, 41],
       shadowSize: [41,41]
    });
    L.Marker.prototype.options.icon = DefaultIcon;

    /**
     * Adjusting map feature (boundary in map)
     * */
    function FitBounds({ locations }) {
        useMapFitBounds(locations);
        return null;
    }

    /**
     * Adjusting map feature (boundary in map)
     * */
    const useMapFitBounds = (locations) => {
        const map = useMap();

        useEffect(()=>{
            if(locations.length>0){
                const bounds = locations
                    .map(loc => [loc.latitude, loc.longitude])
                    .filter(([lat, lng]) => !isNaN(lat) && !isNaN(lng));

                if(bounds.length>0){
                    map.fitBounds(bounds,{padding:[50,50]});
                }
            }
        },[locations, map]);
    };


    /**
    * Logout. Remove sessions and storing last login.
    * */
    const logout = (()=>{
        const username = sessionStorage.getItem("username");
        const loginTime = sessionStorage.getItem("loginTime");
        fetch(`/lastlogin/${username}/${loginTime}`,{
            method: "GET",
            headers:{
                "Content-Type": "application/json",
            }
        })
            .then((res)=>{
                return res.json();
            })
            .catch((error)=>{
                console.log(error);
            });
        sessionStorage.removeItem("username");
        sessionStorage.removeItem("loginTime");
        console.log("logout complete - session(username): " + sessionStorage.getItem("username"));
        navi("/");
    });

    /**
     * Move to Mypage
     * */
    const mypage = (()=>{
        navi("/mypage");
    });

    /**
     * Initialize page by checking login status and fetching hotels' information.
     * */
    useEffect(()=>{
        const username = sessionStorage.getItem("username");
        if(!username){
            alert("Login is required to access");
            navi("/");
            return;
        }

        fetch("/hotels", {
            method: "GET",
            headers : {
                "Content-Type": "application/json",
            },
        })
            .then((res) => {
                return res.json();
            })
            .then((data)=>{
                setHotelList(data);
                setFilteredHotelList(data);
            })
            .catch((error)=>{
                console.log(error);
            })
    }, []);

    /**
     *Search the hotels that contain keywords or hotel number.
     * */
    const performSearch = () =>{
        const filtered = filteredHotelList.filter((hotel)=>{
            const isIdMatch = hotel.hotelId.toString().includes(searchInput);
            const isNameMatch = hotel.hotelName.toLowerCase().includes(searchInput.toLowerCase());
            return isIdMatch || isNameMatch;
        });
        setHotelList(filtered);

        const locations = filtered.map((hotel) => ({
            latitude : hotel.latitude,
            longitude: hotel.longitude,
            hotelName: hotel.hotelName,
            hotelId: hotel.hotelId
        }))
        setFilteredLocations(locations);
    }

    /**
     *If the user enters after fill in search bar it processes the search.
     * */
    const handleKeyDown = (e) =>{
        if(e.key ==="Enter"){
            performSearch();
        }
    };

    /**
     *When hotel name is clicked, the related information is fetched.
     * @param hotelId The unique ID of the hotel
     * @param hotelName The name of the hotel
     * @param hotelAddress The street address of the hotel
     * @param city The city where the hotel is located
     *
     * */
    const handleHotelClick = (hotelId, hotelName, hotelAddress, city, latitude, longitude) => {
        fetch(`/reviews/${hotelId}`,{
            method: "GET",
            headers:{
                "Content-Type": "application/json",
            }
        })
            .then((res)=>{
                return res.json();
            })
            .then((data)=>{
                setSelectedHotelReviews(data);
                setSelectedHotelName(hotelName);
                setSelectedHotelId(hotelId);
                setSelectedHotelAddress(hotelAddress);
                setSelectedHotelCity(city);
                setSelectedHotelLatitude(latitude);
                setSelectedHotelLongitude(longitude);
                setIsModalOpen(true);
                setCalculatedAverageRating(calculatedAverageRating);

                getComments();

            })
            .catch((error)=>{
                console.log(error);
            });
    };

    /**
     *Close the modal that is used to show reviews
     * */
    const closeModal= ()=>{
        setIsModalOpen(false);
        setSelectedHotelReviews([]);
        setSelectedHotelName("");
        setCurrentPage(1);
    }

    /**
     *To track status of modal by leaving logs.
     * */
    useEffect(() => {
        console.log("isModalOpen has changed: ", isModalOpen);
    }, [isModalOpen]);

    /**
     *Calculate the average of hotel rating
     * */
    const calculateAverageRating = () => {
        if(!Array.isArray(selectedHotelReviews)){
            return "NA";
        }
        const totalRating = selectedHotelReviews.reduce((sum, review) => sum + review.overallRating, 0);
        return (totalRating / selectedHotelReviews.length).toFixed(2);
    }

    /**
     *Generates expedia link by formatting.
     * @param hotel The object of hotel used to parse related information.
     * */
    const generateExpediaLink = (hotel) => {
        const formattedName = hotel.name.split(" ").join("-");
        const formattedCity = hotel.city.split(" ").join("-");
        return `https://www.expedia.com/${formattedCity}-Hotels-${formattedName}.h${hotel.id}.Hotel-Information`;
    }

    /**
     *Submitting review, letting it could be saved and updated on the page.
     * */
    const handleReviewSubmit = (e) => {
        e.preventDefault();

        const getFormattedDate = () => {
            const now = new Date();
            const year = now.getFullYear();
            const month = String(now.getMonth() + 1).padStart(2, "0");
            const day = String(now.getDate()).padStart(2,"0");
            return `${year}-${month}-${day}`;
        };


        const newReview = {
            reviewId: uuidv4(),
            hotelId: selectedHotelId,
            reviewTitle: newReviewTitle,
            overallRating : parseFloat(newReviewRating),
            reviewText : newReviewText,
            userNickname: sessionStorage.getItem("username"),
            reviewDate :getFormattedDate(),
        };


        fetch(`/reviews/${selectedHotelId}`,{
            method:"POST",
            headers:{
                "Content-Type": "application/json",
            },
            body: JSON.stringify(newReview),
        })
            .then((res)=>{
            if(res.ok){
                fetch(`/reviews/${selectedHotelId}`,{
                    method:"GET",
                    headers: {
                        "Content-Type": "application/json",
                    },
                })
                 .then((res)=>res.json())
                 .then((data)=>{
                     setSelectedHotelReviews(data);
                 })
                 .catch((error)=>console.log(error));

                setNewReviewTitle("");
                setNewReviewRating("");
                setNewReviewText("");
            }else{
                console.log("Failed to save the review");
            }
        })
        .catch((error)=>{
            console.log(error);
        });
    };

    /**
     *Update the average rating of hotel by tracking changes of the variable selectedHotelReviews.
     * */
    useEffect(()=> {
        const updatedAverageRating = calculateAverageRating();
        setCalculatedAverageRating(updatedAverageRating);
    }, [selectedHotelReviews]);

    /**
     * Reflect the edited review
     * @param index The number of selected hotel review.
     * */
    const handleEditReview = (index)=>{
        const reviewToEdit = selectedHotelReviews[index];
        setNewEditReviewTitle(reviewToEdit.reviewTitle);
        setNewEditReviewRating(reviewToEdit.overallRating);
        setNewEditReviewText(reviewToEdit.reviewText);
        setEditIndex(index);
    };

    /**
     * Save the edited review by http PUT request.
     * */
    const handleSaveEditedReview = () =>{
        if(editIndex===null) return;

        const reviewToUpdate = {
            ...selectedHotelReviews[editIndex],
            reviewTitle: newEditReviewTitle,
            overallRating : parseFloat(newEditReviewRating),
            reviewText: newEditReviewText,
        };

        fetch(`/reviews/${selectedHotelId}`,{
            method:"PUT",
            headers: {
                "Content-Type" : "application/jsoin",
            },
            body:JSON.stringify(reviewToUpdate),
        })
            .then((res)=>{
                if(res.ok){
                    const updatedReviews = [...selectedHotelReviews];
                    updatedReviews[editIndex] = reviewToUpdate;
                    setSelectedHotelReviews(updatedReviews);
                    setEditIndex(null);
                    setNewEditReviewTitle("");
                    setNewEditReviewRating("");
                    setNewEditReviewText("");
                    alert("Review updated successfully!");
                }else{
                    console.error("Failed to update review");
                }
            })
            .catch((error)=>{
               console.error(error);
            });
    };

    /**
     * Delete the selected review by HTTP DELETE request.
     * */
    const handleDeleteReview = (reviewId) => {
        if(!window.confirm("Delete this review?")){
            return;
        }

        fetch(`/reviews/${reviewId}`,{
            method:"DELETE",
            headers:{
                "Content-Type" : "application/jsoin",
            },
        })
        .then((res)=>{
            if(res.ok){
                const updatedReviews = selectedHotelReviews.filter(
                    (review) => review.reviewId !== reviewId
                );
                setSelectedHotelReviews(updatedReviews);
                alert("Review deleted");
            }else{
                alert("Failed to delete review");
                console.error("Failed to delete review");
            }
        })
            .catch((error)=>{
               console.error("Error deleting review",error);
            });
    };

    /**
     * Add Favorite hotels
     * @param selectedHotelId The selected hotel
     * */
    const addFavorite = (selectedHotelId) =>{
        const dto = {
            username : sessionStorage.getItem("username"),
            hotelId : selectedHotelId,
        };
        fetch(`/favorite`,{
            method:"POST",
            headers: {
                "Content-Type" : "application/json",
            },
            body:JSON.stringify(dto),
        })
            .then((res)=>{
                if(res.ok){
                    alert("Favorite Hotel added successfully!");
                }else{
                    console.error("Failed to add favorite hotel");
                }
            })
            .catch((error)=>{
                console.error("Error adding favorite hotel" + error);
            });
    }


    /**
     * Save clicked Expedia link
     * @param link The link of clicked webpage
     * */
    const saveExpediaLink = (link) => {
        const dto = {
            username : sessionStorage.getItem("username"),
            link : link,
        };

        fetch(`/expedia`,{
            method:"POST",
            headers:{
                "Content-Type" : "application/json",
            },
            body:JSON.stringify(dto),
        })
            .then((res)=>{
                if(res.ok){
                    alert("Expedia link added successfully!");
                }else{
                    console.error("Failed to add expedia link");
                }
            })
        .catch((error)=>{
            console.log(error);
        });
    }

    /**
     * Handling clicked page (pagination).
     * @param pageNumber The current page number
     * */
    const handlePageClick = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    /**
     * Booking the hotel
     *
     * */
    const submitBooking = (e) => {
        e.preventDefault();

        const boookingRequest = {
            hotelId : selectedHotelId,
            username : sessionStorage.getItem("username"),
            startDate : startDate,
            endDate : endDate,
        };

        fetch('/bookings', {
            method:"POST",
            headers : {
                'Content-Type' : 'application/json',
            },
            body: JSON.stringify(boookingRequest),
        })
        .then((res)=>{
            if(res.ok){
                alert("Booking added successfully!");
            }else{
                alert("Rooms are already taken for the date");
            }
        })
        .catch((error) => {
            console.error(error)
        });
    }


    /**
     * Add comment in the review
     * @param reviewId The review that comment is being written.
     * */
    const addComment = (e, reviewId) => {
        e.preventDefault();

        const username = sessionStorage.getItem("username");
        const dto = {
            reviewId : reviewId,
            username : username,
            commentText : newComment
        };

        fetch('/comments', {
            method : 'Post',
            headers : {'Content-Type': 'application/json'},
            body: JSON.stringify(dto),
        })
        .then(data => {
            console.log('Response data:', data);
            alert("Comment added successfully!");
            setNewComment('');
            getComments();
        })
        .catch(error => console.error('Error adding comment:', error));
    }


    /**
     * Get comments
     *
     * */
    const getComments = () => {
        fetch(`/comments`, {
            method: "GET"
        })
        .then((res)=>res.json())
        .then(data => {
            const formattedData = data.map(comment => ({
                ...comment,
                commentDate : comment.commentDate? comment.commentDate.split(".")[0] : comment.commentDate
            }))
            setComments(formattedData);
        })
        .catch((error) => {console.error("Error getting  Comments:", error)})
    }

    return(
        <div>
            <div className="hotels-header">
                <button className="hotels-header-logoutbtn" onClick={logout}>Logout</button>
                <button className="hotels-header-mypagebtn" onClick={mypage}>MyPage</button>
                &nbsp;&nbsp;&nbsp; Welcome, {sessionStorage.getItem("username")} !
            </div>

            <div className="hotels-search">
            <input
                    type="text"
                    className="hotels-search-input"
                    placeholder="Search"
                    value={searchInput}
                    onChange={(e)=>setSearchInput(e.target.value)}
                    onKeyDown={handleKeyDown}
                />
                <button className="hotels-search-button" onClick={performSearch}>Search</button>
            </div>

            <div className = "hotels-body">

                {filteredLocations.length > 0 && (
                    <div className="hotels-body-map" style={{height:'60vh', width:'66%', marginTop: '20px'}}>

                        <MapContainer
                            center={[filteredLocations[0]?.latitude || 0, filteredLocations[0]?.longitude || 0]}
                            zoom={13}
                            style={{height:'60vh', width:'70%'}}
                        >
                            <FitBounds locations={filteredLocations} />

                            <TileLayer
                                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                            />
                            {filteredLocations.map((location, index)=>(
                                <Marker
                                    key={index}
                                    position={[location.latitude, location.longitude]}
                                >

                                    <Popup>
                                        <h3>{location.hotelName}</h3>
                                    </Popup>
                                </Marker>
                            ))}
                        </MapContainer>
                    </div>
                )}


                {hotelList.map((data, index)=> (
                    <div className="hotels-body-list">
                        <div className="hotels-body-list-hotelname"
                        onClick={()=> handleHotelClick(data.hotelId, data.hotelName, data.streetAddress, data.city, data.latitude, data.longitude)}
                        style={{cursor:'pointer'}}
                        >{data.hotelName}</div>
                        <div className="hotels-body-list-hoteladdreess">{data.streetAddress}</div>
                        <div className="hotels-body-list-hotelcity">{data.city}</div>
                        <div className="hotels-body-list-hotelcity">HotelId: {data.hotelId}</div>
                    </div>
                ))}
            </div>

            {isModalOpen &&(
                <div className="hotels-modal">
                    <div className="hotels-modal-content">
                        <span className="hotels-modal-close" onClick={closeModal}>
                            X
                        </span>
                        <h1>Hotel Name : {selectedHotelName}</h1>
                        <h3>Hotel ID : {selectedHotelId}</h3>
                        <h3>Hotel Address : {selectedHotelAddress}</h3>
                        <h3>Average rating : {calculatedAverageRating}</h3>
                        <h3>
                            Expedia Link:{" "}
                            <a href={generateExpediaLink({
                                id: selectedHotelId,
                                name: selectedHotelName,
                                city: selectedHotelCity,
                            })}
                               onClick={() => saveExpediaLink(generateExpediaLink({
                                   id: selectedHotelId,
                                   name: selectedHotelName,
                                   city: selectedHotelCity,
                               }))}
                            >
                                Visit Expedia
                            </a>
                        </h3>
                        <button className="hotels-modal-content-bookmarkbtn" onClick={() => addFavorite(selectedHotelId)} style={{cursor : 'pointer'}}>Bookmark this hotel</button>


                        <div style={{height:'40vh', width:'100%'}}>
                            <MapContainer
                                center={[selectedHotelLatitude,selectedHotelLongitude]}
                                zoom={13}
                                style={{height:'40vh', width:'70%'}}
                            >
                                <TileLayer
                                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                                />

                                <Marker
                                    key={selectedHotelId}
                                    position={[selectedHotelLatitude,selectedHotelLongitude]}
                                >
                                </Marker>

                            </MapContainer>
                        </div>


                        <form onSubmit={submitBooking}>
                            <h2>Book a Hotel</h2>
                            <input
                                type="date"
                                value={startDate}
                                onChange={(e) => setStartDate(e.target.value)}
                                required
                            />
                            <input
                                type="date"
                                value={endDate}
                                onChange={(e) => setEndDate(e.target.value)}
                                required
                            />
                            <button type="submit" style={{cursor : 'pointer'}} className="hotels-modal-content-bookingbtn">Book Hotel</button>
                        </form>


                        <h2>Submit a review: </h2>
                        <form onSubmit={handleReviewSubmit}>
                            <label>
                                Title:
                                <input
                                    type="text"
                                    value={newReviewTitle}
                                    onChange={(e) => setNewReviewTitle(e.target.value)}
                                    required
                                />
                            </label>
                            <label>
                                Rating:
                                <input
                                    type="number"
                                    value={newReviewRating}
                                    onChange={(e) => setNewReviewRating(e.target.value)}
                                    required
                                />
                            </label>
                            <label>
                                Review:
                                <input
                                    type="text"
                                    value={newReviewText}
                                    onChange={(e) => setNewReviewText(e.target.value)}
                                    required
                                />
                            </label>
                            <button type="submit" className="hotels-modal-content-reviewsubmitbtn">Submit Review</button>
                        </form>

                        <h2>List of Reviews: </h2>
                        {Array.isArray(currentReviews) && currentReviews.length > 0 ? (
                            <ul>
                                {currentReviews.map((review, index) => (
                                    <li key={review.reviewId} className="hotels-modal-review-item">

                                        {editIndex === index ? (
                                            <>
                                                <form>
                                                    <label>
                                                        Title : <input type="text" value={newEditReviewTitle}
                                                                       onChange={(e) => setNewEditReviewTitle(e.target.value)}
                                                                       required/>
                                                    </label>
                                                    <label>
                                                        Rating : <input type="number" value={newEditReviewRating}
                                                                        onChange={(e) => setNewEditReviewRating(e.target.value)}
                                                                        required/>
                                                    </label>
                                                    <label>
                                                        Review : <textarea value={newEditReviewText}
                                                                           onChange={(e) => setNewEditReviewText(e.target.value)}
                                                                           required/>
                                                    </label>
                                                    <button
                                                        type="button"
                                                        onClick={handleSaveEditedReview}
                                                    >Save
                                                    </button>
                                                    <button type="button" onClick={() => setEditIndex(null)}>
                                                        Cancel
                                                    </button>
                                                </form>
                                            </>
                                        ) : (
                                            <>
                                                <h3>Title : {review.reviewTitle}</h3>
                                                <p>Rating : {review.overallRating}</p>
                                                <p>Review : {review.reviewText}</p>
                                                <p>Id
                                                    : {review.userNickname ? review.userNickname : "Anonymous"}</p>
                                                <p>Date: {review.reviewDate}</p>
                                                {review.userNickname === sessionStorage.getItem("username") && (
                                                    <>
                                                        <button className="hotels-modal-review-item-editbtn"
                                                                onClick={() => handleEditReview(index)}>
                                                            Edit Review
                                                        </button>
                                                        <button className="hotels-modal-review-item-deletebtn"
                                                                onClick={() => handleDeleteReview(review.reviewId)}>
                                                            Delete Review
                                                        </button>
                                                    </>
                                                )}
                                                <div className="hotels-modal-review-item-comments">
                                                    <form onSubmit={(e)=>addComment(e,review.reviewId)}>
                                                        <input
                                                            type="text"
                                                            onChange={(e)=>setNewComment(e.target.value)}
                                                            placeholder="Write a comment"
                                                            className="hotels-modal-review-items-comments-input"
                                                            required
                                                        />
                                                        <button type="submit" className="hotels-modal-review-item-comments-submitbtn">Add</button>
                                                    </form>
                                                </div>
                                                <h3>Comments</h3>
                                                <ul className="comment-list">
                                                    {comments
                                                        .filter((comment) => comment.reviewId === review.reviewId)
                                                        .map((comment,index)=>(
                                                        <li key={comment.id}>
                                                            <p>Id : {comment.username}  &nbsp; |  &nbsp;  {comment.commentDate}</p>
                                                            <p>content : {comment.commentText}</p>
                                                        </li>
                                                    ))}
                                                </ul>
                                            </>
                                        )}
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p>No reviews available for this hotel.</p>
                        )}

                        <div className = "hotels-modal-pagination">
                            {[...Array(Math.ceil((selectedHotelReviews?.length||0) / itemsPerPage))].map((_,i) =>(
                                <button
                                    key={i}
                                    onClick={() => handlePageClick(i+1)}
                                    disabled={currentPage === i + 1}
                                    className="hotels-modal-pagination-btn"
                                    style={{cursor: 'pointer'}}
                                >
                                    {i + 1}
                                </button>
                                ))}

                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Hotels;

