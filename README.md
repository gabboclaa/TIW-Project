# TIW-Project

Project Summary: Satellite Image Taxonomy Management System

Version in pure HTML:

This application allows environmental service managers to efficiently manage a collection of satellite images through a user-friendly taxonomy system. Users can log in and access a HOME page displaying a shared hierarchical tree of categories. The system supports the addition of new categories, each assigned a unique numeric code to reflect its position in the taxonomy.
To facilitate taxonomy construction, users can copy entire subtrees to specific positions within the hierarchy. This involves selecting a "copy" link associated with the root category of the subtree to be copied. The application then highlights the subtree, and other categories display a "copy here" link. Users can click this link to insert a copy of the subtree as the last child of the chosen destination category.
Changes made by one user are instantly visible to others, promoting real-time collaboration. Each category is limited to a maximum of 9 subcategories. The system checks and prevents moves that would exceed this limit. Additionally, a "copy here" link not associated with a taxonomy node allows copying a subtree to the first level of the taxonomy, 
provided there are fewer than 9 nodes at that level.

Version in JavaScript:

Develop a web client-server application that extends and/or modifies the previous specifications as follows:
- After user login, the entire application is implemented on a single page.
- Every user interaction is handled without fully reloading the page, but instead triggers asynchronous server invocation and potential modification of content to be updated following the event.
- The subtree copy function is implemented using drag & drop. After dropping the root of the subtree to copy, a dialog window appears, allowing the user to confirm or cancel the copy operation. Confirmation results in updating the client-side tree only, while cancellation reverts to the state before the drag & drop. After confirmation, a SAVE button appears, enabling the saving of the modified taxonomy on the server side.
- Users can click on the name of a category. This event replaces the name with an input field containing the editable name string. Losing focus on the input field triggers the saving of the modified category name in the database.

These enhancements make the application more dynamic and responsive, utilizing asynchronous interactions for seamless user experiences. The drag & drop functionality with confirmation and the ability to modify category names directly contribute to a more interactive and user-friendly environment.

Key Features:
- User-friendly taxonomy management
- Hierarchical tree structure
- Subtree copying for efficient taxonomy building
- Real-time collaboration with changes visible to all users

Technologies Used:
- Frontend: HTML, CSS, JavaScript
- Backend: Java Servlets (Java EE)
- Database: MySQL
- Version Control: Git
- Collaboration: Real-time updates through a database

This project streamlines taxonomy management, promoting collaboration and efficiency in categorizing satellite images for environmental monitoring.
