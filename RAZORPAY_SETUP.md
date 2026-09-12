# Razorpay Test Mode Setup Guide

## Getting Razorpay Test Credentials

1. **Sign up for Razorpay**
   - Go to https://razorpay.com/
   - Click on "Sign Up" and create an account
   - Verify your email address

2. **Access Test Mode**
   - After logging in, navigate to Settings → API Keys
   - You'll see two sets of keys:
     - **Test Mode** (for development/testing)
     - **Live Mode** (for production)

3. **Copy Test Credentials**
   - Under Test Mode, you'll find:
     - **Key ID**: Starts with `rzp_test_`
     - **Key Secret**: Starts with `rzp_test_`

4. **Update Configuration**
   - Update the following files with your test credentials:
   
   **In Order-management-service/src/main/resources/application.yml:**
   ```yaml
   razorpay:
     key:
       id: rzp_test_YOUR_ACTUAL_KEY_ID
       secret: rzp_test_YOUR_ACTUAL_KEY_SECRET
   ```
   
   **In docker-compose.yml:**
   ```yaml
   environment:
     - RAZORPAY_KEY_ID=rzp_test_YOUR_ACTUAL_KEY_ID
     - RAZORPAY_KEY_SECRET=rzp_test_YOUR_ACTUAL_KEY_SECRET
   ```

## Razorpay Test Mode Features

### Test Card Numbers
Razorpay provides test card numbers for testing different scenarios:

**Successful Payment:**
- Card Number: 4242 4242 4242 4242
- Expiry: Any future date (e.g., 12/25)
- CVV: Any 3 digits (e.g., 123)

**Failed Payment:**
- Card Number: 4000 0000 0000 0002
- Expiry: Any future date
- CVV: Any 3 digits

**International Card:**
- Card Number: 4000 0012 3456 7890
- Expiry: Any future date
- CVV: Any 3 digits

### Testing Scenarios

1. **Successful Payment Flow**
   - Create order → Process payment → Verify payment → Confirm order

2. **Failed Payment Flow**
   - Create order → Process payment with failed card → Handle failure → Retry payment

3. **Refund Flow**
   - Complete payment → Process refund → Verify refund status

## Important Notes

- **Test Mode**: No real money is deducted
- **Test Dashboard**: View all test transactions in Razorpay dashboard
- **Webhooks**: Test webhooks using Razorpay's webhook simulator
- **Currency**: Test mode supports INR by default
- **Limits**: No transaction limits in test mode

## Security Best Practices

1. **Never commit credentials to version control**
2. **Use environment variables** for sensitive data
3. **Separate test and live credentials**
4. **Rotate keys periodically**
5. **Monitor test dashboard** for unauthorized activity

## Troubleshooting

### Common Issues

1. **Invalid Key Error**
   - Verify key ID and secret are correct
   - Ensure you're using test mode keys, not live keys

2. **Payment Failed**
   - Check test card number is valid
   - Verify expiry date is in the future
   - Ensure CVV is 3 digits

3. **Webhook Not Received**
   - Check webhook URL is accessible
   - Verify webhook secret is correct
   - Use Razorpay webhook simulator for testing

## Next Steps

1. Get your Razorpay test credentials
2. Update configuration files
3. Start all services using docker-compose
4. Test payment flow with test card
5. Verify transactions in Razorpay dashboard
