# CommunicationHub GUI Application

## Overview
This is a complete graphical user interface for the CommunicationHub and User classes that provides a user-friendly way to interact with the application.

## Features

### 1. **LoginFrame** - Authentication & Registration Access
- Requests phone number for login simulation
- Validates phone number format (XXX-XXX-XXXX)
- Searches for the user in the system
- Handles invalid inputs and missing users with appropriate error messages
- **"Register" button** to navigate to RegistrationFrame for new users
- Provides a clean, focused login interface

### 2. **MainGUI** - Main Application Window
- Displays logged-in user information
- Provides tabbed interface for different operations
- Manages multiple panels for different functional areas

### 3. **RegistrationFrame** - New User Registration
- **Complete registration form** with validation:
  - First Name field (required)
  - Last Name field (required)
  - Phone Number field with XXX-XXX-XXXX format validation
- **Comprehensive error handling:**
  - Validates all fields are filled
  - Checks phone number format validity
  - Detects duplicate phone numbers
  - Displays clear error messages
- **Success feedback** with auto-redirect to login after registration
- "Back to Login" button to return without registering
- Seamless integration with CommunicationHub.register() method

### 4. **CommunicationHubOperationsPanel** - CommunicationHub Public Methods
Implements all public methods from CommunicationHub class:
- **register()** - Register new users with validation
- **findUser(String name)** - Search by exact name
- **findUser(PhoneNumber)** - Search by phone number
- **findUsersWithNameSubString()** - Fuzzy name search
- **findUserWithPhonePrefix()** - Search by phone prefix
- **findUserWithPhonePostfix()** - Search by phone postfix
- **findUserWithPhoneSubString()** - Search by phone substring
- **getFullInformation()** - Display all users in system

### 4. **UserProfilePanel** - User Profile Management
Implements public methods from User class:
- **setFirstname()** - Update first name with validation
- **setLastname()** - Update last name with validation
- **getFirstname()** - View first name
- **getLastname()** - View last name
- **getPhoneNumber()** - View phone number
- **getFullInformation()** - View complete profile information
- **Real-time profile updates** after modifications

### 5. **ContactsPanel** - Contact Management
Implements User contact methods:
- **addContact()** - Add contacts with duplicate detection
- **isAContact(String name)** - Check contact by name
- **isAContact(PhoneNumber)** - Check contact by phone
- Validates phone numbers before adding contacts
- Prevents adding duplicate contacts

### 6. **GroupsPanel** - Group Information
- Displays group membership information
- Shows basic group operations interface
- Can expand to support full group functionality

### 8. **Application** - Main Launcher
- Entry point for the application
- Initializes CommunicationHub with sample data for testing
- Creates login interface on startup

## User Flow
1. **Application starts** → Application.java creates LoginFrame
2. **User has two options:**
   - **Option A (Existing User):** Enter phone number → Login → Access MainGUI
   - **Option B (New User):** Click "Register" button → Fill registration form → RegistrationFrame validates and registers → Auto-redirect to LoginFrame
3. After successful login → MainGUI displays with tabbed interface
4. User interacts with panels → Methods called on CommunicationHub/User instances
5. Results displayed in text areas with error handling

## Input Validation & Error Handling

### Registration Validation (New Feature)
- Format: XXX-XXX-XXXX
- Uses `PhoneNumber.isValid()` for validation
- Displays appropriate error messages for invalid formats

### Exception Handling
- All operations wrapped in try-catch blocks
- User-friendly error messages displayed in result areas
- No unhandled exceptions crash the application
- Input validation before processing user requests

### Data Validation
- Empty field checks
- Null pointer handling
- Duplicate detection for contacts and registrations
- Business logic validation (e.g., can't add existing contact)

## GUI Controls Used
- **JFrame** - Main window containers
- **JPanel** - Layout and organization
- **JTabbedPane** - Multi-section interface
- **JTextField** - Text input fields
- **JButton** - Action buttons
- **JTextArea** - Display results and information
- **JLabel** - Field labels
- **JList** - Contact and user list display
- **JScrollPane** - Scrollable content areas
- **Borders** - Visual organization with titled borders
- **GridLayout** / **GridBagLayout** - Organized control arrangement

## How to Run

1. Compile all Java files:
   ```
   javac -d . view/*.java model/*.java
   ```

2. Run the application:
   ```
   java view.Application
   ```

3. Login with one of the pre-registered users:
   - 555-123-4567 (John Doe)
   - 555-234-5678 (Jane Smith)
   - 555-345-6789 (Bob Jones)
   - 555-456-7890 (Alice Williams)
   - 555-567-8901 (Charlie Brown)

## Data Flow
1. Application starts → Application.java
2. LoginFrame displayed → User enters phone number
3. Validation occurs → Lookup in CommunicationHub system
4. Success → MainGUI opens with tabs
5. User interacts with panels → Methods called on CommunicationHub/User instances
6. Results displayed in text areas with error handling

## Implementation Coverage

✅ **Complete GUI Implementation**
- All public methods from CommunicationHub callable through GUI
- All public methods from User callable through GUI
- Same instances passed to all GUI classes
- Login simulation with phone number validation
- Comprehensive input validation
- Complete exception handling
- 100% GUI implementation

### Methods Implemented:
**CommunicationHub Methods:**
- register ✅
- findUser (by name) ✅
- findUser (by phone) ✅
- findUsersWithNameSubString ✅
- findUserWithPhonePrefix ✅
- findUserWithPhonePostfix ✅
- findUserWithPhoneSubString ✅
- getFullInformation ✅

**User Methods:**
- setFirstname ✅
- setLastname ✅
- getFirstname ✅
- getLastname ✅
- getPhoneNumber ✅
- getFullInformation ✅
- addContact ✅
- isAContact (name) ✅
- isAContact (phone) ✅

## Notes
- The GUI is designed to be extensible for future enhancements
- All panels receive the same CommunicationHub and logged-in User instances
- Results are displayed in designated result areas for user feedback
- The application demonstrates proper MVC-like architecture
