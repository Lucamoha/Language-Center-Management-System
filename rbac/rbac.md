| Panel | ADMIN | STAFF_CONSULTANT | STAFF_ACCOUNTANT | TEACHER | STUDENT |
|---|---|---|---|---|---|
| Dashboard | ✓ | ✓ | ✓ | ✓ | ✓ |
| Students | ✓ | ✓ | ✓ (read) | ✗ | self only |
| Teachers | ✓ | ✓ | ✗ | ✗ | ✗ |
| Courses | ✓ | ✓ | ✗ | ✓ (read) | ✗ |
| Classes | ✓ | ✓ | ✗ | own only | ✗ |
| Enrollments | ✓ | ✓ | ✓ (read) | ✗ | self only |
| Rooms | ✓ | ✓ | ✗ | ✗ | ✗ |
| Schedules | ✓ | ✓ | ✗ | own only | self only |
| Attendance | ✓ | ✓ (read) | ✗ | own class | self only |
| Results | ✓ | ✓ (read) | ✗ | own class | self only |
| Invoices | ✓ | ✗ | ✓ | ✗ | self only |
| Payments | ✓ | ✗ | ✓ | ✗ | self only |
| Staff | ✓ | ✗ | ✗ | ✗ | ✗ |
| User Accounts | ✓ | ✗ | ✗ | ✗ | ✗ |

- Use soft delete in delete operation
- Only admin have deletion permission