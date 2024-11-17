

## PhotoVault API - README

Welcome to the **PhotoVault API**, a backend service for managing albums and photos in the PhotoVault web application. This API provides secure and efficient endpoints for users to create, view, manage, and download photos and albums.

---

### **Features**
- Create and manage photo albums.
- Upload, view, and delete photos.
- Secure photo download functionality.
- User authentication and authorization.
- Dockerized for easy setup and deployment.

---

### **Technologies Used**
- **Java Spring Boot**: Backend framework.
- **H2 Database**: Embedded database for development.
- **JPA/Hibernate**: ORM for database operations.
- **Swagger/OpenAPI**: API documentation.
- **Spring Security**: Authentication and authorization.
- **RESTful APIs**: Interface design.
- **Docker**: Containerization for easy deployment.

---

### **API Endpoints**

#### **Album Endpoints**
1. **Get all albums**  
   `GET /albums`  
   _Description_: Retrieve a list of all albums.  
   _Authorization_: Requires authentication.

2. **Get album by ID**  
   `GET /albums/{album_id}`  
   _Description_: Retrieve album details, including photos.  
   _Authorization_: User must own the album.

3. **Create album**  
   `POST /albums`  
   _Description_: Create a new photo album.  
   _Authorization_: Requires authentication.  
   _Body Example_:  
   ```json
   {
     "name": "Vacation Album",
     "description": "Photos from my summer vacation."
   }
   ```

4. **Delete album**  
   `DELETE /albums/{album_id}`  
   _Description_: Delete an album and its photos.  
   _Authorization_: User must own the album.

---

#### **Photo Endpoints**
1. **Upload photo**  
   `POST /albums/{album_id}/photos`  
   _Description_: Upload a photo to a specific album.  
   _Authorization_: User must own the album.  
   _Form Data_:  
   - `name`: Name of the photo.  
   - `description`: Description of the photo.  
   - `file`: Photo file (JPEG, PNG).

2. **View photo details**  
   `GET /albums/{album_id}/photos/{photo_id}`  
   _Description_: Retrieve details of a specific photo.  
   _Authorization_: User must own the album.

3. **Download photo**  
   `GET /albums/{album_id}/photos/{photo_id}/download-photo`  
   _Description_: Download a specific photo from an album.  
   _Authorization_: User must own the album.  
   _Response_: Returns the photo as a binary file.

4. **Delete photo**  
   `DELETE /albums/{album_id}/photos/{photo_id}/delete`  
   _Description_: Delete a specific photo from an album.  
   _Authorization_: User must own the album.

---

### **Authentication**
This API uses **Spring Security** for user authentication and authorization.  
- Authentication is done via a token-based mechanism.
- Include the token in the `Authorization` header for secured endpoints:  
  ```
  Authorization: Bearer <token>
  ```

---

### **Setup and Installation**

#### **With Docker**
1. **Clone the repository**:
   ```bash
   git clone https://github.com/username/photovault-api.git
   cd photovault-api
   ```

2. **Build the Docker image**:
   ```bash
   docker build -t photovault-api .
   ```

3. **Run the Docker container**:
   ```bash
   docker run -p 8080:8080 photovault-api
   ```

4. **Access API documentation**:
   Navigate to [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) for interactive API documentation.

#### **Without Docker**
1. **Clone the repository**:
   ```bash
   git clone https://github.com/username/photovault-api.git
   cd photovault-api
   ```

2. **Configure the application**:
   - Update the `application.properties` file to match your database configuration if required.

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

---

### **Error Responses**
- `400 Bad Request`: Invalid input parameters.
- `401 Unauthorized`: User authentication failed.
- `403 Forbidden`: User does not have permission for the requested resource.
- `404 Not Found`: Resource not found.
- `500 Internal Server Error`: Unexpected server error.

---

### **Future Enhancements**
- Add support for video uploads.
- Implement photo tagging and search functionality.
- Enhance photo sharing options.

---

### **Disclaimer**
This API is part of a **student project** and is not intended for production use. It is built for educational purposes to demonstrate skills in backend development and API design.

---

For any issues or questions, please contact [ratshalingwaadivhaho106@gmail.com](mailto:ratshalingwaadivhaho106@gmail.com).
