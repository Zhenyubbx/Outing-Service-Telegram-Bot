# import everything
import constants 
from telegram.ext import Updater, CommandHandler, MessageHandler, Filters
from utils.LoggingUtil import Logging
from services.ServiceIntegration import ServiceIntegration
from telegram import ReplyKeyboardMarkup
from dotenv import load_dotenv
import constants
import threading
import os

# Log Errors caused by updates
# def error(update, context):
#     service_logger.error('Update "%s" caused error "%s"', update, context.error)

# Command Handlers
def start(update, context, keyboard):
    response = ("Hello, Thank you for using the Outing Service Telegram Bot! Please select an option in the keyboard!")
    update.message.reply_text(response, reply_markup = ReplyKeyboardMarkup(keyboard,
                                       one_time_keyboard=True,
                                       resize_keyboard=True))

# Main
def main():
    load_dotenv()
    global updater, service_logger
    updater = Updater(os.getenv('BOT_TOKEN'), use_context=True)
    service_logger = Logging.create_service_log(__name__)
    service_logger.info("Starting the server...")

    # Get the dispatcher to register handlers
    dp = updater.dispatcher
        
    keyboard = []
    # Add Command Handlers
    dp.add_handler(CommandHandler("start", lambda update, context: start(update, context, keyboard)))

    ServiceIntegration(dp=dp, keyboard=keyboard)

    # Log all errors
    # dp.add_error_handler(error)

    # Start the Bot
    updater.start_polling()
    service_logger.info("Server started!")

    updater.idle()


if __name__ == '__main__':
    main()