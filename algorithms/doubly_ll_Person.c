#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct Person
{
    char name[50];
    int age;
    char occupation[50];
};

struct Node
{
    struct Person data;
    struct Node *prev;
    struct Node *next;
};

struct Node *createNode(struct Person data)
{
    struct Node *newNode = (struct Node *)malloc(sizeof(struct Node));
    newNode->data = data;
    newNode->prev = NULL;
    newNode->next = NULL;
    return newNode;
}

void insertAtBeginning(struct Node **headRef, struct Person data)
{
    struct Node *newNode = createNode(data);
    newNode->next = *headRef;
    if (*headRef != NULL)
        (*headRef)->prev = newNode;
    *headRef = newNode;
}

void insertAtEnd(struct Node **headRef, struct Person data)
{
    struct Node *newNode = createNode(data);
    if (*headRef == NULL)
    {
        *headRef = newNode;
        return;
    }
    struct Node *temp = *headRef;
    while (temp->next != NULL)
        temp = temp->next;
    temp->next = newNode;
    newNode->prev = temp;
}

void displayForward(struct Node *head)
{
    printf("Doubly Linked List (Forward):\n");
    while (head != NULL)
    {
        printf("Name: %s, Age: %d, Occupation: %s\n", head->data.name, head->data.age, head->data.occupation);
        head = head->next;
    }
    printf("\n");
}

void displayBackward(struct Node *tail)
{
    printf("Doubly Linked List (Backward):\n");
    while (tail != NULL)
    {
        printf("Name: %s, Age: %d, Occupation: %s\n", tail->data.name, tail->data.age, tail->data.occupation);
        tail = tail->prev;
    }
    printf("\n");
}

void addPerson(struct Node **headRef)
{
    struct Person newPerson;
    printf("Enter your name: ");
    fgets(newPerson.name, sizeof(newPerson.name), stdin);
    newPerson.name[strcspn(newPerson.name, "\n")] = 0; // Remove trailing newline
    printf("Enter your age: ");
    scanf("%d", &newPerson.age);
    getchar(); // Consume newline character left in input buffer
    printf("Enter your occupation: ");
    fgets(newPerson.occupation, sizeof(newPerson.occupation), stdin);
    newPerson.occupation[strcspn(newPerson.occupation, "\n")] = 0; // Remove trailing newline

    struct Node *newNode = createNode(newPerson);

    // If the list is empty, the new node becomes the head
    if (*headRef == NULL)
    {
        *headRef = newNode;
        return;
    }

    struct Node *temp = *headRef;
    while (temp->next != NULL)
        temp = temp->next;
    temp->next = newNode;
    newNode->prev = temp;
}

int main()
{
    struct Node *head = NULL;

    while (1)
    {
        int choice = 1;

        addPerson(&head);

        printf("Do you want to continue (1/0) or check the linked list : ");
        scanf("%d", &choice);

        if (choice == 0)
        {
            char innerchoice;
            getchar(); // Consume newline character left in input buffer
            printf("Display forward or the display backward (f or b): ");
            scanf(" %c", &innerchoice); // Add space before %c to consume whitespace characters
            if (innerchoice == 'f')
            {
                displayForward(head);
            }
            else if (innerchoice == 'b')
            {
                struct Node *tail = head;
                while (tail->next != NULL)
                    tail = tail->next;
                displayBackward(tail);
            }
            break;
        }
    }
    return 0;
}
