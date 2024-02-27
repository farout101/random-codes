#include <stdio.h>
#include <string.h>

// Encrypt
void encrypt(char *plaintext, int shift) {
    int i;
    for(i = 0; i < strlen(plaintext); i++) {
        if(plaintext[i] >= 'A' && plaintext[i] <= 'Z') {
            plaintext[i] = ((plaintext[i] - 'A' + shift) % 26) + 'A';
        }
        else if(plaintext[i] >= 'a' && plaintext[i] <= 'z') {
            plaintext[i] = ((plaintext[i] - 'a' + shift) % 26) + 'a';
        }
    }
}

// Decrypt
void decrypt(char *ciphertext, int shift) {
    int i;
    for(i = 0; i < strlen(ciphertext); i++) {
        if(ciphertext[i] == ' ') {
            continue;
        }
        if(ciphertext[i] >= 'A' && ciphertext[i] <= 'Z') {
            ciphertext[i] = ((ciphertext[i] - 'A' - shift + 26) % 26) + 'A';
        }
        else if(ciphertext[i] >= 'a' && ciphertext[i] <= 'z') {
            ciphertext[i] = ((ciphertext[i] - 'a' - shift + 26) % 26) + 'a';
        }
    }
}

int main() {
    char message[50];

    printf("Enter a message to encrypt: ");
    fgets(message, sizeof(message), stdin);

    int shift = 3; // Shift by 3 positions

    message[strcspn(message, "\n")] = 0;

    printf("Original message: %s\n", message);

    encrypt(message, shift);
    printf("Encrypted message: %s\n", message);

    decrypt(message, shift);
    printf("Decrypted message: %s\n", message);

    return 0;
}
