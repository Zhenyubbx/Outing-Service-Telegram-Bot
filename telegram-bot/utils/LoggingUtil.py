import logging
from logging import FileHandler, Formatter

class Logging:
    def __init__(self):
        self.type = ["service"]
        self.service_log = False
        self.message_log = False
        self.service_logger = None
        self.message_logger = None


    def create_service_log(name):
        LOG_FORMAT = ("%(asctime)s [%(levelname)s]: '%(message)s' in %(filename)s:%(lineno)d")
        # LOG_FORMAT = ("%(asctime)s [%(levelname)s]: '%(message)s' in %(pathname)s") #:%(lineno)d
        SERVICE_LOG_FILE = "logs/service.log"
        LOG_LEVEL = logging.INFO

        service_logger = logging.getLogger(name + " service")
        service_logger.setLevel(LOG_LEVEL)
        service_logger_file_handler = FileHandler(SERVICE_LOG_FILE)
        service_logger_file_handler.setLevel(LOG_LEVEL)
        service_logger_file_handler.setFormatter(Formatter(LOG_FORMAT))
        service_logger.addHandler(service_logger_file_handler)

        return service_logger
